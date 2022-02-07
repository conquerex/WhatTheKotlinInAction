package ch4;

public class SampleButton implements View {

    @Override
    public State getCurrentState() {
        return new ButtonState();
    }

    @Override
    public void restoreState(final State state) {
        //
    }

    public class ButtonState implements State {
        //
    }

}