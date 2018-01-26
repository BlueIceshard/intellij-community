import org.jetbrains.annotations.Contract;4

class CompoundAssignmentSideEffect {

  int k;

  void m() {
    pureCall(nonPureCall1(), nonPureCall2()).b &= true<caret>;
  }

  @Contract(pure = true)
  X pureCall(int i, int j) {
    return new X();
  }

  int nonPureCall1() {
    return k;
  }

  int nonPureCall2() {
    return k;
  }

  class X {
    boolean b = false;
  }
}