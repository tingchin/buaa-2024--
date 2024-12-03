package utils;

import java.util.Iterator;

public class MyList<N, L> implements Iterable<MyNode<N, L>> {
    private MyNode<N, L> head;
    private MyNode<N, L> tail;
    private final L value;
    private int size;

    public MyList(L value) {
        this.head = null;
        this.value = value;
        this.tail = null;
        this.size = 0;
    }

    public MyNode<N, L> getHead() {
        return head;
    }

    public void setHead(MyNode<N, L> head) {
        this.head = head;
    }

    public MyNode<N, L> getTail() {
        return tail;
    }

    public void setTail(MyNode<N, L> tail) {
        this.tail = tail;
    }

    public L getValue() {
        return value;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean isEmpty() {
        return (this.getHead() == null) && (this.getTail() == null) && (getSize() == 0);
    }

    public void addNode() {
        this.size++;
    }

    public void removeNode() {
        this.size--;
    }

    @Override
    public Iterator<MyNode<N, L>> iterator() {
        return new ListIterator(this.getHead());
    }

    class ListIterator implements Iterator<MyNode<N, L>> {
        MyNode<N, L> now = new MyNode<>(null);
        MyNode<N, L> next = null;

        public ListIterator(MyNode<N, L> head) {
            now.setNext(head);
        }

        @Override
        public boolean hasNext() {
            return next != null || now.getNext() != null;
        }

        @Override
        public MyNode<N, L> next() {
            if (next == null) {
                now = now.getNext();
            } else {
                now = next;
            }
            next = null;
            return now;
        }

        @Override
        public void remove() {
            MyNode<N, L> prev = now.getPrev();
            MyNode<N, L> next = now.getNext();
            MyList<N, L> parent = now.getParent();
            if (prev != null) {
                prev.setNext(next);
            } else {
                parent.setHead(next);
            }
            if (next != null) {
                next.setPrev(prev);
            } else {
                parent.setTail(prev);
            }
            parent.removeNode();
            this.next = next;
            now.clear();
        }
    }
}
