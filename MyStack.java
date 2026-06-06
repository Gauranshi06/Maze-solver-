import java.util.EmptyStackException;

/**
 * A custom implementation of a Last-In-First-Out (LIFO) Stack.
 * Built using a singly linked node structure to avoid utilizing standard collections.
 *
 * @param <T> The type of elements held in this stack.
 */
public class MyStack<T> {

    // Helper node class for linking elements
    private static class Node<T> {
        final T data;
        Node<T> next;

        Node(T data) {
            this.data = data;
            this.next = null;
        }
    }

    private Node<T> top;

    /**
     * Constructs an empty stack.
     */
    public MyStack() {
        this.top = null;
    }

    /**
     * Pushes an item onto the top of this stack.
     *
     * @param item The item to be pushed.
     */
    public void push(T item) {
        Node<T> newNode = new Node<>(item);
        newNode.next = top;
        top = newNode;
    }

    /**
     * Removes the object at the top of this stack and returns that
     * object as the value of this function.
     *
     * @return The item popped from the stack.
     * @throws EmptyStackException if this stack is empty.
     */
    public T pop() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        T data = top.data;
        top = top.next;
        return data;
    }

    /**
     * Looks at the object at the top of this stack without removing it
     * from the stack.
     *
     * @return The item at the top of the stack.
     * @throws EmptyStackException if this stack is empty.
     */
    public T peek() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return top.data;
    }

    /**
     * Tests if this stack is empty.
     *
     * @return true if and only if this stack contains no items; false otherwise.
     */
    public boolean isEmpty() {
        return top == null;
    }
}
