package com.energy.community.guiapp.model;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;

public class EnergyCommunityModel {

    public EnergyCommunityModel() {
        // Listener with lambda
        bool.addListener((o, oldVal, newVal) -> {
            boolToVisibility.invalidate();
        });

    }

    private BooleanProperty bool = new SimpleBooleanProperty();

    private BooleanBinding boolToVisibility = new BooleanBinding() {
        @Override
        protected boolean computeValue() {
            return bool.get();
        }
    };

    public final BooleanProperty boolProperty() {
        return bool;
    }

    public BooleanBinding boolToVisibilityBinding() {
        return boolToVisibility;
    }

}
