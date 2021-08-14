// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.staticCities;

import org.terasology.gestalt.entitysystem.component.Component;
import org.terasology.staticCities.settlements.Settlement;

/**
 * Indicates a settlement.
 */
public final class SettlementComponent implements Component<SettlementComponent> {

    public Settlement settlement;

    public SettlementComponent() {
    }

    public SettlementComponent(Settlement settlement) {
        this.settlement = settlement;
    }

    @Override
    public void copyFrom(SettlementComponent other) {
        this.settlement = other.settlement;
    }
}
