package jary.rules.game

class Fire {

    String name

    boolean isRetractable

    long duration

    Fire(String name, long duration) {
        this.name = name
        this.duration = duration
        this.isRetractable = true
    }
}
