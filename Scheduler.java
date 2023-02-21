package hw1;

//ZEYNEP CINDEMIR - 201401012
public class Scheduler {

    //Creating a timeCount variable to keep track of time
    public static int timeCount = 0;

    //Creating a jobCounter variable to keep track of the jobs
    public static int jobCounter = 0;

    //Creating a runFlag variable to keep track of loops
    public static boolean runFlag = true;

    //Creating (waitlist)
    public static DoublyLinkedList<job> waitList = new DoublyLinkedList<>();

    //Creating totalPQueue to keep all the jobs in order of priority
    public static DoublyLinkedList<job> totalPQueue = new DoublyLinkedList<>();

    //Creating (highPQueue), (midPQueue) and (lowPQueue)
    public static DoublyLinkedList<job> highPQueue = new DoublyLinkedList<>();
    public static DoublyLinkedList<job> midPQueue = new DoublyLinkedList<>();
    public static DoublyLinkedList<job> lowPQueue = new DoublyLinkedList<>();
    public static ArrayListStructure.ArrayList<job> jobDatabase = new ArrayListStructure.ArrayList<>();

    //Creating a Resource ArrayList to keep all the resources
    public static ArrayListStructure.ArrayList<Resource> resources = new ArrayListStructure.ArrayList<>();


    //Adding specified number of resources to the Resource ArrayList
    public void setResourcesCount(int resourcesCount) {
        for (int i = 1; i <= resourcesCount; i++) {
            Resource kaynak = new Resource(i);
            resources.add(kaynak);
        }
    }

    //Adding jobs to waitlist and jobDatabase in order of arrival time
    public void add(job j) {
        jobDatabase.add(j);

        if (waitList.isEmpty()) {
            waitList.addFirst(j);
        } else {
            DoublyLinkedList.Node<job> walk = waitList.getHeadNode();

            do {
                walk = walk.getNext();
            } while (walk.getElement() != null && walk.getElement().arrivalTime <= j.arrivalTime);

            waitList.addBetween(j, walk.getPrev(), walk);
        }
    }

    //Printing specific resource's utilization
    public void utilization(int resourceNo) {
        System.out.print(resources.get(resourceNo - 1) + " verim: ");
        System.out.printf("%.2f\n", resources.get(resourceNo - 1).calcUtilization());
    }

    //Printing specific resource's jobs' properties
    public void resourceExplorer(int resourceNo) {
        int id, end, delay;
        Resource tmp = resources.get(resourceNo - 1);

        System.out.print(tmp + " : ");
        for (int i = 0; i < tmp.jobList.size(); i += tmp.jobList.get(i).duration) {
            id = tmp.jobList.get(i).id;
            end = tmp.ends.get(i);
            delay = tmp.delays.get(i);
            if (i == tmp.jobList.size() - tmp.jobList.get(i).duration) {
                System.out.print("(" + id + "," + end + "," + delay + ")");
            } else {
                System.out.print("(" + id + "," + end + "," + delay + "), ");
            }
        }
        System.out.println();
    }

    //Printing specific job's properties
    public void jobExplorer(job j) {
        Resource kaynak = null;
        int baslangic = 0;
        int bitis = 0;
        int gecikme = 0;

        System.out.println("islemno kaynak  baslangic  bitis  gecikme");

        for (int i = 0; i < resources.size(); i++)
            if (resources.get(i).jobList.contains(j))
                kaynak = resources.get(i);

        if (kaynak != null)
            for (int i = 0; i < kaynak.jobList.size(); i++)
                if (kaynak.jobList.get(i).id == j.id) {
                    baslangic = kaynak.starts.get(i);
                    bitis = kaynak.ends.get(i);
                    gecikme = kaynak.delays.get(i);
                    System.out.println(j.id + "\t\t" + kaynak + "\t\t" + baslangic + "\t\t   " + bitis + "\t  " + gecikme);
                    return;
                }
    }

    //Distributing all the jobs to the PQueues in order of priority
    public void checkPQ() {
        DoublyLinkedList.Node<job> walk = waitList.getHeadNode();
        while (walk.getElement() != null) {
            if (walk.getElement().priority.equals("H") && walk.getElement().arrivalTime == timeCount)
                if (!highPQueue.containJob(walk.getElement())) {
                    highPQueue.addLast(walk.getElement());
                    totalPQueue.addLast(walk.getElement());
                }

            walk = walk.getNext();
        }

        walk = waitList.getHeadNode();
        while (walk.getElement() != null) {
            if (walk.getElement().priority.equals("M") && walk.getElement().arrivalTime == timeCount)
                if (!midPQueue.containJob(walk.getElement())) {
                    midPQueue.addLast(walk.getElement());
                    totalPQueue.addLast(walk.getElement());
                }

            walk = walk.getNext();
        }

        walk = waitList.getHeadNode();
        while (walk.getElement() != null) {
            if (walk.getElement().priority.equals("L") && walk.getElement().arrivalTime == timeCount)
                if (!lowPQueue.containJob(walk.getElement())) {
                    lowPQueue.addLast(walk.getElement());
                    totalPQueue.addLast(walk.getElement());
                }

            walk = walk.getNext();
        }
    }

    //Checking for the end of all the jobs
    public boolean continueRun() {
        if (jobCounter == jobDatabase.size())
            runFlag = false;

        return runFlag;
    }

    //Starting simulation & timeline
    public void run() {
        System.out.print("Zaman\t");
        for (int i = 0; i < resources.size(); i++) {
            System.out.print("R" + (i + 1) + "\t");
        }

        while (runFlag) {
            checkPQ();
            DoublyLinkedList.Node<job> temp = totalPQueue.getHeadNode();
            System.out.print("\n");
            executeByPriority(temp);
        }
    }

    //Executing events, timeline & managing lists
    private void executeByPriority(DoublyLinkedList.Node<job> walk) {
        System.out.printf("%5d \t", timeCount);
        for (int i = 0; i < resources.size(); i++) {
            Resource r = resources.get(i);
            //       TIME =    0     1     2     3     4     5
            // R1 jobList = { J0-H, J0-H, J0-H, J3-H, J3-H, J3-H ... } Adding elements to the end of the list as many as duration
            // R2 jobList = { J1-H, J1-H, J2-M, J2-M, J2-M,  -   ... }
            // R1 starts  = {  0,    0,    0,    3,    3,    3,  ... } Adding specific job's starting time
            // R1 ends    = {  2,    2,    2,    5,    5,    5,  ... } Adding specific job's ending time
            // R2 starts  = {  0,    0,    2,    2,    2,    -   ... }
            // R2 ends    = {  1,    1,    2,    4,    4,    -   ... }


            if (r.ends.size() != 0 && r.ends.get(r.ends.size() - 1) < timeCount)
                r.isBusy = false;

            if (r.isBusy)
                System.out.print("J" + r.jobList.get(r.jobList.size() - 1).id + "\t");
            else {
                if ((r.jobList.isEmpty() || r.jobList.size() <= timeCount) && walk.getElement() != null) {
                    System.out.print("J" + walk.getElement().id + "\t");

                    for (int j = 0; j < walk.getElement().duration; j++) {
                        r.jobList.add(walk.getElement());
                        r.starts.add(timeCount);
                        r.ends.add(timeCount + walk.getElement().duration - 1);
                        r.delays.add(timeCount - walk.getElement().arrivalTime);
                    }

                    DoublyLinkedList.Node<job> tempTotal = totalPQueue.getHeadNode();
                    while (tempTotal.getElement() != null) {
                        if (tempTotal.getElement().id == r.getJobList().get(r.ends.size() - 1).id)
                            totalPQueue.remove(tempTotal);

                        tempTotal = tempTotal.getNext();
                    }

                    DoublyLinkedList.Node<job> temp;
                    switch (r.getJobList().get(r.ends.size() - 1).priority) {
                        case "H":
                            temp = highPQueue.getHeadNode();
                            while (temp.getElement() != null) {
                                if (temp.getElement().id == r.getJobList().get(r.ends.size() - 1).id)
                                    highPQueue.remove(temp);

                                temp = temp.getNext();
                            }
                            break;

                        case "M":
                            temp = midPQueue.getHeadNode();
                            while (temp.getElement() != null) {
                                if (temp.getElement().id == r.getJobList().get(r.ends.size() - 1).id)
                                    midPQueue.remove(temp);

                                temp = temp.getNext();
                            }
                            break;

                        case "L":
                            temp = lowPQueue.getHeadNode();
                            while (temp.getElement() != null) {
                                if (temp.getElement().id == r.getJobList().get(r.ends.size() - 1).id)
                                    lowPQueue.remove(temp);

                                temp = temp.getNext();
                            }
                            break;
                    }

                    r.isBusy = true;
                    walk = walk.getNext();
                } else
                    System.out.print("  \t");
            }

            if (r.ends.size() != 0 && r.ends.get(r.ends.size() - 1) == timeCount) {
                jobCounter++;

                if (!continueRun()) {
                    System.out.print("\n");
                    break;
                }
            }
        }
        timeCount++;
        checkPQ();
    }

    //Inner DoublyLinkedList class implementation
    public static class DoublyLinkedList<E> {

        private final Node<E> header;
        private final Node<E> trailer;
        private int size = 0;

        public DoublyLinkedList() {
            header = new Node<>(null, null, null);
            trailer = new Node<>(null, header, null);
            header.setNext(trailer);
        }

        public int size() {
            return size;
        }

        public boolean isEmpty() {
            return size == 0;
        }

        public E first() {
            if (isEmpty()) return null;
            return header.getNext().getElement();
        }

        public Node<E> getHeadNode() {
            return header.getNext();
        }

        public Node<E> getTailNode() {
            return trailer.getPrev();
        }

        public E last() {
            if (isEmpty()) return null;
            return trailer.getPrev().getElement();
        }

        public void addBefore(Node<E> node, E newData) {
            addBetween(newData, node.getPrev(), node);
        }

        public void removeBefore(Node<E> node) {
            remove(node.getPrev());
        }

        public void addFirst(E e) {
            addBetween(e, header, header.getNext());
        }

        public void addLast(E e) {
            addBetween(e, trailer.getPrev(), trailer);
        }

        public E removeFirst() {
            if (isEmpty()) return null;
            return remove(header.getNext());
        }

        public E removeLast() {
            if (isEmpty()) return null;
            return remove(trailer.getPrev());
        }

        private void addBetween(E e, Node<E> predecessor, Node<E> successor) {
            Node<E> newest = new Node<>(e, predecessor, successor);
            predecessor.setNext(newest);
            successor.setPrev(newest);
            size++;
        }

        private E remove(Node<E> node) {
            Node<E> predecessor = node.getPrev();
            Node<E> successor = node.getNext();
            predecessor.setNext(successor);
            successor.setPrev(predecessor);
            size--;
            return node.getElement();
        }

        public boolean containJob(job j) {
            if (this.size == 0)
                return false;
            else {
                Node<E> walk = this.getHeadNode();
                for (int i = 0; i < this.size; i++) {
                    if (((job) walk.getElement()).id == j.id)
                        return true;
                }
            }

            return false;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder("(");
            Node<E> walk = header.getNext();
            while (walk != trailer) {
                sb.append(walk.getElement());
                walk = walk.getNext();
                if (walk != trailer)
                    sb.append(", ");
            }
            sb.append(")");
            return sb.toString();
        }

        //Inner Node class implementation
        private static class Node<E> {

            private E element;
            private Node<E> prev;
            private Node<E> next;

            public Node(E e, Node<E> p, Node<E> n) {
                element = e;
                prev = p;
                next = n;
            }

            public E getElement() {
                return element;
            }

            public Node<E> getPrev() {
                return prev;
            }

            public void setPrev(Node<E> p) {
                prev = p;
            }

            public Node<E> getNext() {
                return next;
            }

            public void setNext(Node<E> n) {
                next = n;
            }
        }
    }

    //Inner Resource class implementation
    public static class Resource {

        //Creating ArrayLists to keep starting, ending & delay values of the jobs
        private ArrayListStructure.ArrayList<Integer> starts;
        private ArrayListStructure.ArrayList<Integer> ends;
        private ArrayListStructure.ArrayList<Integer> delays;
        private int num;
        private int resourceRuntime;
        private boolean isBusy;

        //Creating jobList to keep the resource's jobs
        private ArrayListStructure.ArrayList<job> jobList;


        public Resource(int num) {
            this.num = num;
            this.isBusy = false;
            this.resourceRuntime = 0;
            this.jobList = new ArrayListStructure.ArrayList<>();
            this.starts = new ArrayListStructure.ArrayList<>();
            this.ends = new ArrayListStructure.ArrayList<>();
            this.delays = new ArrayListStructure.ArrayList<>();
        }

        @Override
        public String toString() {
            return "R" + num;
        }

        //Calculating the resource's runtime
        public int calcRuntime() {
            int tempID = -1;

            if (this.jobList.size() > 0) {
                for (int i = 0; i < this.jobList.size(); i++) {
                    if (tempID != this.jobList.get(i).id) {
                        resourceRuntime += this.jobList.get(i).duration;
                        tempID = this.jobList.get(i).id;
                    }
                }
            }

            return resourceRuntime;
        }

        //Calculating the resource's utilization
        public double calcUtilization() {
            return (double) calcRuntime() / (this.ends.get(ends.size - 1) + 1);
        }

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }

        public ArrayListStructure.ArrayList<job> getJobList() {
            return jobList;
        }

        public void setJobList(ArrayListStructure.ArrayList<job> jobList) {
            this.jobList = jobList;
        }
    }

    //Inner ArrayListStructure class implementation
    public static class ArrayListStructure {
        interface RandomAccess {}
        interface List<T> {
            void add(T e);

            void add(int index, T element);

            void addAll(T[] c);

            void addAll(int index, T[] c) throws IndexOutOfBoundsException;

            T get(int index) throws IndexOutOfBoundsException;

            T remove(int index) throws IndexOutOfBoundsException;

            void set(int index, T element);

            int indexOf(T o);

            int size();

            T[] toArray();
        }

        //Inner ArrayList class implementation
        public static class ArrayList<T> implements List<T>, RandomAccess {

            private T[] a;
            private int size;


            public ArrayList() {
                this.a = (T[]) new Object[10];
            }

            public void add(T e) {
                if (this.size + 1 >= this.a.length) {
                    this.resize();
                }
                this.a[this.size()] = e;
                this.size++;
            }

            public void add(int index, T element) {
                if (index >= 0 || index < this.size()) {
                    if (this.size() + 1 > a.length)
                        this.resize();

                    for (int i = this.size(); i > index; i--)
                        this.set(i, this.get(i - 1));

                    this.set(index, element);
                    size++;

                } else
                    throw new IndexOutOfBoundsException("index " + index + " is beyond the size of the array (" + this.size() + ")");
            }

            public void addAll(T[] c) {
                for (T element : c) {
                    add(element);
                }
            }

            public void addAll(int index, T[] c) throws IndexOutOfBoundsException {
                int i = index;
                for (T element : c) {
                    add(i, element);
                    i++;
                }
            }

            public T get(int index) throws IndexOutOfBoundsException {
                if (index >= 0 && index < this.size())
                    return a[index];
                else
                    throw new IndexOutOfBoundsException("index " + index + " is beyond the size of the array (" + this.size() + ")");
            }

            public T remove(int index) throws IndexOutOfBoundsException {
                if (index >= 0 && index < this.size()) {
                    T temp = a[index];

                    for (int i = index; i < this.size() - 1; i++)
                        a[index] = a[index + 1];

                    size--;
                    return temp;
                } else
                    throw new IndexOutOfBoundsException("index " + index + " is beyond the size of the array (" + this.size() + ")");
            }

            public void set(int index, T element) {
                if ((index >= 0) || (index < this.size())) {
                    this.a[index] = element;
                } else
                    throw new IndexOutOfBoundsException("index " + index + " is beyond the size of the array (" + this.size() + ")");
            }

            public int indexOf(T o) {
                for (int i = 0; i < this.size(); i++) {
                    if (this.a[i].equals(o))
                        return i;
                }

                return -1;
            }

            public int size() {
                return this.size;
            }

            public T[] toArray() {
                return this.a;
            }

            private void resize() {
                //Creating new array with doubled size
                T[] resizedArray = (T[]) new Object[this.a.length * 2];

                //Copying elements of internal array into new array
                for (int i = 0; i < this.size(); i++)
                    resizedArray[i] = this.a[i];

                //Setting internal array to new array
                this.a = resizedArray;
            }

            @Override
            public String toString() {
                StringBuilder out = new StringBuilder();
                for (int i = 0; i < this.size(); i++)
                    out.append("[").append(get(i).toString()).append("]");

                return out.toString();
            }

            public boolean isEmpty() {
                return this.size() == 0;
            }

            public boolean contains(T element) {
                if (this.isEmpty())
                    return false;

                for (int i = 0; i < this.size; i++) {
                    if (this.get(i) == element)
                        return true;
                }
                return false;
            }
        }
    }
}