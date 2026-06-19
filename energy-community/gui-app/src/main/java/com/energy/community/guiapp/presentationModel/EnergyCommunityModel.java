package com.energy.community.guiapp.presentationModel;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.paint.Color;


public class EnergyCommunityModel {

    public EnergyCommunityModel() {
        // Listener mit Lambda
        bool.addListener((o, oldVal, newVal) -> {
            boolInverted.invalidate();
            boolToVisibility.invalidate();
        });


    }

    private BooleanProperty bool = new SimpleBooleanProperty();

    private BooleanBinding boolInverted = new BooleanBinding() {
        @Override
        protected boolean computeValue() {
            return !bool.get();
        }
    };

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
