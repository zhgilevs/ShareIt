package ru.practicum.shareit.booking;

import ru.practicum.shareit.exception.UnsupportedStatusException;

public enum State {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static State parseState(String state) {
        if (state == null || state.isBlank()) {
            return State.ALL;
        }
        for (State enumState : State.values()) {
            if (enumState.name().equalsIgnoreCase(state)) {
                return enumState;
            }
        }
        throw new UnsupportedStatusException("Unknown state: " + state);
    }
}