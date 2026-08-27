package com.energy.community.usage.service;

import com.energy.community.usage.entity.EnergyEntity;
import com.energy.community.usage.repository.EnergyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsageServiceTest {

        @Mock
        private EnergyRepository energyRepository;

        @Mock
        private RabbitTemplate rabbitTemplate;

        private UsageService usageService;

        @BeforeEach
        void setUp() {
                EnergyMessageParser energyMessageParser = new EnergyMessageParser();

                usageService = new UsageService(
                                energyRepository,
                                energyMessageParser,
                                rabbitTemplate);
        }

        @Test
        void producerEnergyIsAddedToHourlyProduction() {
                when(energyRepository.save(any(EnergyEntity.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                LocalDateTime expectedHour = LocalDateTime.of(2026, 8, 27, 14, 0);

                when(energyRepository.findByHour(expectedHour))
                                .thenReturn(Optional.empty());

                String message = """
                                {
                                  "type": "PRODUCER",
                                  "association": "COMMUNITY",
                                  "kwh": 1.2345,
                                  "datetime": "2026-08-27T14:23:45"
                                }
                                """;

                usageService.processEnergyMessage(message);

                ArgumentCaptor<EnergyEntity> captor = ArgumentCaptor.forClass(EnergyEntity.class);

                verify(energyRepository).save(captor.capture());

                EnergyEntity savedEntity = captor.getValue();

                assertEquals(expectedHour, savedEntity.getHour());
                assertEquals(
                                1.235,
                                savedEntity.getCommunityProduced(),
                                0.000001);
                assertEquals(
                                0.0,
                                savedEntity.getCommunityUsed(),
                                0.000001);
                assertEquals(
                                0.0,
                                savedEntity.getGridUsed(),
                                0.000001);
        }

        @Test
        void userUsesCommunityEnergyBeforeGridEnergy() {
                when(energyRepository.save(any(EnergyEntity.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                LocalDateTime hour = LocalDateTime.of(2026, 8, 27, 14, 0);

                EnergyEntity existingEntity = createEntity(
                                hour,
                                2.0,
                                0.5,
                                0.0);

                when(energyRepository.findByHour(hour))
                                .thenReturn(Optional.of(existingEntity));

                String message = """
                                {
                                  "type": "USER",
                                  "association": "COMMUNITY",
                                  "kwh": 1.0,
                                  "datetime": "2026-08-27T14:30:00"
                                }
                                """;

                usageService.processEnergyMessage(message);

                assertEquals(
                                1.5,
                                existingEntity.getCommunityUsed(),
                                0.000001);

                assertEquals(
                                0.0,
                                existingEntity.getGridUsed(),
                                0.000001);
        }

        @Test
        void remainingUsageComesFromGrid() {
                when(energyRepository.save(any(EnergyEntity.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                LocalDateTime hour = LocalDateTime.of(2026, 8, 27, 14, 0);

                EnergyEntity existingEntity = createEntity(
                                hour,
                                1.0,
                                0.8,
                                0.1);

                when(energyRepository.findByHour(hour))
                                .thenReturn(Optional.of(existingEntity));

                String message = """
                                {
                                  "type": "USER",
                                  "association": "COMMUNITY",
                                  "kwh": 0.5,
                                  "datetime": "2026-08-27T14:45:00"
                                }
                                """;

                usageService.processEnergyMessage(message);

                assertEquals(
                                1.0,
                                existingEntity.getCommunityUsed(),
                                0.000001);

                assertEquals(
                                0.4,
                                existingEntity.getGridUsed(),
                                0.000001);
        }

        @Test
        void invalidCommunityMessageIsIgnored() {
                String message = """
                                {
                                  "type": "USER",
                                  "association": "OTHER",
                                  "kwh": 1.0,
                                  "datetime": "2026-08-27T14:30:00"
                                }
                                """;

                usageService.processEnergyMessage(message);

                verifyNoInteractions(
                                energyRepository,
                                rabbitTemplate);
        }

        @Test
        void datetimeIsAssignedToCorrectHour() {
                when(energyRepository.save(any(EnergyEntity.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                LocalDateTime expectedHour = LocalDateTime.of(2026, 8, 27, 21, 0);

                when(energyRepository.findByHour(expectedHour))
                                .thenReturn(Optional.empty());

                String message = """
                                {
                                  "type": "PRODUCER",
                                  "association": "COMMUNITY",
                                  "kwh": 0.5,
                                  "datetime": "2026-08-27T21:59:59"
                                }
                                """;

                usageService.processEnergyMessage(message);

                verify(energyRepository)
                                .findByHour(expectedHour);
        }

        @Test
        void producerArrivingAfterUserReallocatesGridUsage() {
                when(energyRepository.save(any(EnergyEntity.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                LocalDateTime hour = LocalDateTime.of(2026, 8, 27, 14, 0);

                EnergyEntity existingEntity = createEntity(
                                hour,
                                0.0,
                                0.0,
                                1.0);

                when(energyRepository.findByHour(hour))
                                .thenReturn(Optional.of(existingEntity));

                String message = """
                                {
                                  "type": "PRODUCER",
                                  "association": "COMMUNITY",
                                  "kwh": 1.0,
                                  "datetime": "2026-08-27T14:40:00"
                                }
                                """;

                usageService.processEnergyMessage(message);

                assertEquals(
                                1.0,
                                existingEntity.getCommunityProduced(),
                                0.000001);

                assertEquals(
                                1.0,
                                existingEntity.getCommunityUsed(),
                                0.000001);

                assertEquals(
                                0.0,
                                existingEntity.getGridUsed(),
                                0.000001);
        }

        private EnergyEntity createEntity(
                        LocalDateTime hour,
                        double produced,
                        double used,
                        double grid) {
                EnergyEntity entity = new EnergyEntity();

                entity.setHour(hour);
                entity.setCommunityProduced(produced);
                entity.setCommunityUsed(used);
                entity.setGridUsed(grid);

                return entity;
        }
}