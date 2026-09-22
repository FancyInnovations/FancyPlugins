package com.fancyinnovations.fancyworlds.portals.selection;

public record PortalSelection(PortalSelectionPoint first, PortalSelectionPoint second) {

    public boolean isComplete() {
        return first != null && second != null;
    }

    public boolean isInOneWorld() {
        return isComplete() && first.worldName().equalsIgnoreCase(second.worldName());
    }

    public long getVolume() {
        if (!isComplete()) {
            return 0;
        }

        return (long) (Math.abs(first.position().x() - second.position().x()) + 1)
                * (Math.abs(first.position().y() - second.position().y()) + 1)
                * (Math.abs(first.position().z() - second.position().z()) + 1);
    }
}
