// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.staticCities;

import org.terasology.engine.entitySystem.Component;
import org.terasology.staticCities.settlements.Settlement;

/**
 * Indicates a settlement.
 */
public final class SettlementComponent implements Component {

    public Settlement settlement;

    public SettlementComponent() {
    }

    public SettlementComponent(Settlement settlement) {
        this.settlement = settlement;
    }
}
