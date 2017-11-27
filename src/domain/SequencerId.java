package domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SequencerId{
    private final String id;

    public String getId() {
        return id;
    }
}
