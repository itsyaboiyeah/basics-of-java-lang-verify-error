This repo demonstrates how to get an error like

```
Exception in thread "main" java.lang.VerifyError: Stack map does not match the one at exception handler 26
Exception Details:
  Location:
    A.f(Ljava/lang/String;)LC; @26: astore
  Reason:
    Type 'D' (current frame, locals[2]) is not assignable to 'C' (stack map, locals[2])
  Current Frame:
    bci: @16
    flags: { }
    locals: { 'A', 'java/lang/String', 'D', 'A' }
    stack: { 'java/lang/Exception' }
  Stackmap Frame:
    bci: @26
    flags: { }
    locals: { 'A', 'java/lang/String', 'C', 'A' }
    stack: { 'java/lang/Exception' }
  Bytecode:
    0000000: bb00 0559 b700 064d bb00 0759 b700 084e
    0000010: bb00 0959 120a b700 0bbf 3a04 b200 0d2c
    0000020: b600 0eb2 000d 2ab4 0004 b600 0eb2 000d
    0000030: 2db6 000e bb00 0f59 b700 10b0
  Exception Handler Table:
    bci [16, 26] => handler: 26
  Stackmap Table:
    full_frame(@26,{Object[#7],Object[#27],Object[#15],Object[#7]},{Object[#12]})

        at java.base/java.lang.Class.forName0(Native Method)
        at java.base/java.lang.Class.forName(Class.java:315)
        at Main.main(Main.java:4)
```

From this you can play around and come to understand the contents of the error.

The above error can be got by being in the 'compiledAndBroken' directory, and running `java Main`. The contents of 'compiledAndBroken' are the result (for my Java setup) of compiling Main.java and modifying the byte 05 at position 0x2c6 in the A.class file to be 0f. What does this do?

This 05 byte is part of the definition of the stack map of the exception handler in the 'f' method of the class A, 00 05 refers to the 5th index of the constant pool of the class, which is 'D', which matches up with the line `D d = new D();`. By changing it to 00 0f, we now making it be the 15th index in the constant pool, which is 'C' (i.e. referring to the class C). Hence the error telling us `Type 'D' (current frame, locals[2]) is not assignable to 'C' (stack map, locals[2])`.

Questions and answers

1. What is a Stack map? Answer: It is a bit of metadata introduced into Java which helps the verification of types at runtime loading of classes (namely, it helps verify the types make sense, prior to this metadata the jvm tried to infer the types and would backtrack several times to make sure the inferred types were consistent). These stack maps appear at specified 'jumps' e.g. exceptions as these are the problem areas.

2. What is the stack map of an exception handler? Answer: Exceptions cause jumps so have a stack map, e.g. try catch blocks have a stack map

3. What does the location refer to? Answer: The location is the class together with potentially the method where the VerifyError was noticed. We have `A.f(Ljava/lang/String;)LC` which tells us it happened verifying the method 'f' of A which takes in a string and returns a C.

4. What are the locals? Answer: They are the types of the local variables, in a method invocation the class in question is pushed on as a local, hence 'A' appearing first, then the methods parameters, in our case java/lang/String, then locally declared variables (in this case the ones in scope of the exception handler)

5. How to interpret the Stackmap table? Answer: In our case we have ` full_frame(@26,{Object[#7],Object[#27],Object[#15],Object[#7]},{Object[#12]})`. It helps to know that index 7 in the constant pool is 'A', 27 is '/java/lang/string', 15 is 'C', 12 is 'java/lang/Exception'

6. How to interpret the bytecode? Answer: It refers to the bytecode around the exception, i.e. in our casse the bytecode listed can be found in A.class
