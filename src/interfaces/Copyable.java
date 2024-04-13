package interfaces;

public interface Copyable<T> {

    /**
     * Copies the object.
     *
     * @return A new instance of the object with the same parameters as the original.
     */
    T copy();
}
