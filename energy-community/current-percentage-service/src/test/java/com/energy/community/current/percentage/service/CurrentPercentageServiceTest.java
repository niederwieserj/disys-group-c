package com.energy.community.current.percentage.service;

import com.energy.community.current.percentage.entity.PercentageEntity;
import com.energy.community.current.percentage.repository.PercentageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrentPercentageServiceTest {

        @Mock
        private PercentageRepository percentageRepository;

        private CurrentPercentageService currentPercentageService;

        @BeforeEach
        void setUp() {
                currentPercentageService = new CurrentPercentageService(percentageRepository);
        }

        @Test
        void calculatesCorrectPercentages() {
                LocalDateTime currentHour = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);

                String message = """
                                {
                                  "hour": "%s",
                                  "community_produced": 10.0,
                                  "community_used": 4.0,
                                  "grid_used": 1.0
                                }
                                """.formatted(currentHour);

                currentPercentageService.processUsageUpdate(message);

                ArgumentCaptor<PercentageEntity> captor = ArgumentCaptor.forClass(PercentageEntity.class);

                verify(percentageRepository).save(captor.capture());

                PercentageEntity savedEntity = captor.getValue();

                assertEquals(currentHour, savedEntity.getHour());

                assertEquals(
                                40.0,
                                savedEntity.getCommunityDepleted(),
                                0.000001);

                assertEquals(
                                20.0,
                                savedEntity.getGridPortion(),
                                0.000001);
        }

        @Test
        void zeroValuesResultInZeroPercentages() {
                LocalDateTime currentHour = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);

                String message = """
                                {
                                  "hour": "%s",
                                  "community_produced": 0.0,
                                  "community_used": 0.0,
                                  "grid_used": 0.0
                                }
                                """.formatted(currentHour);

                currentPercentageService.processUsageUpdate(message);

                ArgumentCaptor<PercentageEntity> captor = ArgumentCaptor.forClass(PercentageEntity.class);

                verify(percentageRepository).save(captor.capture());

                PercentageEntity savedEntity = captor.getValue();

                assertEquals(
                                0.0,
                                savedEntity.getCommunityDepleted(),
                                0.000001);

                assertEquals(
                                0.0,
                                savedEntity.getGridPortion(),
                                0.000001);
        }

        @Test
        void oldHourIsIgnored() {
                LocalDateTime oldHour = LocalDateTime.now()
                                .truncatedTo(ChronoUnit.HOURS)
                                .minusHours(1);

                String message = """
                                {
                                  "hour": "%s",
                                  "community_produced": 10.0,
                                  "community_used": 5.0,
                                  "grid_used": 1.0
                                }
                                """.formatted(oldHour);

                currentPercentageService.processUsageUpdate(message);

                verifyNoInteractions(percentageRepository);
        }

        @Test
        void negativeValuesAreIgnored() {
                LocalDateTime currentHour = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);

                String message = """
                                {
                                  "hour": "%s",
                                  "community_produced": 10.0,
                                  "community_used": -1.0,
                                  "grid_used": 0.0
                                }
                                """.formatted(currentHour);

                currentPercentageService.processUsageUpdate(message);

                verifyNoInteractions(percentageRepository);
        }

        @Test
        void outdatedPercentageRecordsAreRemoved() {
                LocalDateTime currentHour = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);

                PercentageEntity outdatedEntity = new PercentageEntity();
                outdatedEntity.setHour(currentHour.minusHours(1));

                List<PercentageEntity> outdatedEntities = List.of(outdatedEntity);

                when(percentageRepository.findByHourNot(currentHour))
                                .thenReturn(outdatedEntities);

                String message = """
                                {
                                  "hour": "%s",
                                  "community_produced": 5.0,
                                  "community_used": 2.0,
                                  "grid_used": 1.0
                                }
                                """.formatted(currentHour);

                currentPercentageService.processUsageUpdate(message);

                verify(percentageRepository)
                                .delete(outdatedEntity);

                verify(percentageRepository)
                                .save(any(PercentageEntity.class));
        }

        @Test
        void malformedMessageIsIgnored() {
                String message = "this is not valid json";

                currentPercentageService.processUsageUpdate(message);

                verifyNoInteractions(percentageRepository);
        }
}