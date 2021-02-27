public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("Going to try to load A");
        Class.forName("A");
        System.out.println("All good");
    }
}

class A {
    E e = new E();
    C f(String x) {
        D d = new D();
        A acop = new A();
        try {
            throw new RuntimeException("Whoops");
        } catch (Exception ex) {
            System.out.println(d);
            System.out.println(e);
            System.out.println(acop);
        }
        return new C();
    }
}

class C {

}

class D {

}

class E {

}