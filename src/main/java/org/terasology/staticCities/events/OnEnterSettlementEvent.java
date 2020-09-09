// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.staticCities.events;

import org.terasology.engine.entitySystem.event.Event;
import org.terasology.staticCities.settlements.Settlement;

public class OnEnterSettlementEvent implements Event {
    private final Settlement settlement;

    /**
     * @param settlement the settlement that was entered
     */
    public OnEnterSettlementEvent(Settlement settlement) {
        this.settlement = settlement;
    }

    /**
     * @return the area that was entered
     */
    public Settlement getSettlement() {
        return settlement;
    }
}
