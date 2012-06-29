package com.fillumina.performance;

import java.io.Serializable;

/**
 * It allows to print out to console without having to enclose
 * a long code into {@code System.out.println()}. The beauty of it
 * is that it doesn't change the preexisting code that used a {@link String}.
 *
 * @author fra
 */
public class OutputHolder implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String output;

    public OutputHolder(final String output) {
        this.output = output;
    }

    public void println() {
        System.out.println(output);
    }

    @Override
    public String toString() {
        return output;
    }
}
