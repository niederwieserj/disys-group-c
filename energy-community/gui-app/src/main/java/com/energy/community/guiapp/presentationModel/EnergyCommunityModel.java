package com.energy.community.guiapp.presentationModel;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;


public class EnergyCommunityModel {

    public EnergyCommunityModel() {
        // Listener mit Lambda
        bool.addListener((o, oldVal, newVal) -> {
            boolToVisibility.invalidate();
        });

    }

    private BooleanProperty bool = new SimpleBooleanProperty();

    private BooleanBinding boolToVisibility = new BooleanBinding() {
        @Override
        protected boolean computeValue() {
            // hier noch was machen wie im SimpleBindings bsp
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
