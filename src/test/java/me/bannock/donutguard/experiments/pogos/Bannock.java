package me.bannock.donutguard.experiments.pogos;

public class Bannock extends Food implements Animal {

    public Bannock(){
        super("Bannock");
    }

    @Override
    public void eat() {
        System.out.println("Does not taste good");
        asdf();
        asdfg();
        super.eat();
    }

    private void asdf(){
        makeNoise();
    }

    private static void asdfg(){

    }

    @Override
    public int getHealth() {
        super.getHealth();
        return 1;
    }

    @Override
    public void makeNoise() {
        System.out.println("Rawr");
    }

}
