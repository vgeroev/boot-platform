package org.vmalibu.modules.utils;

public record Version(int major, int minor, int patch) implements Comparable<Version> {

    public Version {
        if (major < 0 || minor < 0 || patch < 0) {
            throw new IllegalArgumentException("Invalid version values");
        }
    }

    @Override
    public int compareTo(Version o) {
        int diff = major - o.major;
        if (diff != 0) {
            return diff;
        }

        diff = minor - o.minor;
        if (diff != 0) {
            return diff;
        }

        return patch - o.patch;
    }

    @Override
    public String toString() {
        return "%d.%d.%d".formatted(major, minor, patch);
    }
}
