import java.util.NoSuchElementException;

/**
 * A custom implementation of a First-In-First-Out (FIFO) Queue.
 * Built using a singly linked node structure with head and tail pointers
 * to achieve O(1) enqueue and dequeue operations.
 *
 * @param <T> The type of elements held in this queue.
 */
public class MyQueue<T> {

    // Helper node class for linking elements
    private static class Node<T> {
        final T data;
        Node<T> next;

        Node(T data) {
            this.data = data;
            this.next = null;
        }
    }

    private Node<T> head;
    private Node<T> tail;

    /**
     * Constructs an empty queue.
     */
    public MyQueue() {
        this.head = null;
        this.tail = null;
    }

    /**
     * Inserts the specified element at the end of this queue.
     *
     * @param item The item to enqueue.
     */
    public void enqueue(T item) {
        Node<T> newNode = new Node<>(item);
        if (isEmpty()) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }
    }

    /**
     * Retrieves and removes the head of this queue.
     *
     * @return The item dequeued from the head.
     * @throws NoSuchElementException if this queue is empty.
     */
    public T dequeue() {
        if (isEmpty()) {
            throw new NoSuchElementException("Queue is empty");
        }
        T data = head.data;
        head = head.next;
        if (head == null) {
            tail = null;
        }
        return data;
    }

    /**
     * Retrieves, but does not remove, the head of this queue.
     *
     * @return The item at the head of the queue.
     * @throws NoSuchElementException if this queue is empty.
     */
    public T peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("Queue is empty");
        }
        return head.data;
    }

    /**
     * Tests if this queue is empty.
     *
     * @return true if and only if this queue contains no items; false otherwise.
     */
    public boolean isEmpty() {
        return head == null;
    }
}
