# Elektronika MK-61 Virtual Machine
Elektronika MK‑61 Virtual Machine (VM) is a simple software emulator of the programmable calculator Elektronika MK‑61. The original MK‑61 is a third‑generation calculator with Reverse Polish notation (RPN). It was produced from 1983 to 1994 and used for engineering and scientific calculations. The real device has 105 steps of volatile program memory and 15 memory registers.

The physical MK‑61 was manufactured in Kyiv at the “Kristall” factory. The chief designer was A. Sherevenya, the deputy main engineer was A. Perutskiy, and the head of the acceptance committee was V. Kostikov.

This project is a stack‑based VM that tries to follow the behavior of the original MK‑61 as closely as possible. It is implemented as a standalone Kotlin Multiplatform library that you can use inside other applications.

---

## Artifacts status

### Validation
[![Codemagic vm-tests status](https://api.codemagic.io/apps/6a5e0d2168781379aa421c52/vm-tests/status_badge.svg)](https://codemagic.io/app/6a5e2824997989ee564bfba7/vm-tests/latest_build)

### Deployment
[![Codemagic vm-publish status](https://api.codemagic.io/apps/6a5e0d2168781379aa421c52/vm-publish/status_badge.svg)](https://codemagic.io/app/6a5e2824997989ee564bfba7/vm-publish/latest_build)

---

## Integration

```toml
[versions]
mk61-vm = "0.1.2"

[libraries]
mk61-virtual-machine = { module = "io.github.valiuh.mk61:virtual-machine", version.ref = "mk61-vm" }
```

The snippet above shows how to declare the dependency in `libs.versions.toml`.  
After that, you can use it in your `build.gradle.kts`:

```kotlin
dependencies {
    implementation(libs.mk61.virtual.machine)
}
```

If you are not using the Kotlin DSL, you can add it directly in `build.gradle`:

```groovy
dependencies {
    implementation "io.github.valiuh.mk61:virtual-machine:0.1.2"
}
```

For Maven, add the following to your `pom.xml`:

```xml
<dependency>
    <groupId>io.github.valiuh.mk61</groupId>
    <artifactId>virtual-machine</artifactId>
    <version>0.1.2</version>
</dependency>
```
---

# Virtual Machine Specification
# Instruction Set

The virtual machine implements the original Elektronika MK-61 instruction set.

All instruction mnemonics are preserved exactly as in the original calculator documentation.

---

## Stack Operations

| Instruction | Description |
|------------|-------------|
| `В↑` | Push the current value of **X** onto the stack. The stack changes as `T → Z → Y → X`. |
| `↻` | Rotate the stack. The stack changes as `T → Z → Y → X → T`. |
| `↔` | Exchange the values of **X** and **Y**. |
| `Вx` | Restore the previous result (**Last X**) into **X** while lifting the stack. |
| `СX` | Clear register **X**. |

---

## Arithmetic

| Instruction | Description |
|------------|-------------|
| `+` | Calculate `Y + X`. Store the result in **X**. |
| `-` | Calculate `Y − X`. Store the result in **X**. |
| `×` | Calculate `Y × X`. Store the result in **X**. |
| `÷` | Calculate `Y ÷ X`. Store the result in **X**. |

---

## Mathematical Functions

| Instruction | Description |
|------------|-------------|
| `10^x` | Calculate `10^X`. |
| `e^x` | Calculate `e^X`. |
| `lg` | Calculate the base-10 logarithm of **X**. |
| `ln` | Calculate the natural logarithm of **X**. |
| `sin` | Calculate the sine of **X**. |
| `cos` | Calculate the cosine of **X**. |
| `tg` | Calculate the tangent of **X**. |
| `sin^-1` | Calculate the arcsine of **X**. |
| `cos^-1` | Calculate the arccosine of **X**. |
| `tg^-1` | Calculate the arctangent of **X**. |
| `√` | Calculate the square root of **X**. |
| `1/x` | Calculate the reciprocal of **X**. |
| `x^2` | Square **X**. |
| `x^y` | Raise **Y** to the power of **X**. |

---

## Constants

| Instruction | Description |
|------------|-------------|
| `π` | Load the constant π into **X**. |
| `e` | Load the constant Euler's number *e* into **X**. |

---

## Numeric Functions

| Instruction | Description |
|------------|-------------|
| `[x]` | Extract the integer part of **X**. |
| `{x}` | Extract the fractional part of **X**. |
| `\|x\|` | Calculate the absolute value of **X**. |
| `max` | Return the larger value of **X** and **Y**. |
| `ЗН` | Return the sign of **X** (`-1`, `0`, or `1`). |

---

## Angle Conversion

| Instruction | Description |
|------------|-------------|
| `°←'` | Convert degrees (hours), minutes and fractional minutes into decimal degrees (hours). |
| `°→'` | Convert decimal degrees (hours) into degrees (hours), minutes and fractional minutes. |
| `°←''"` | Convert degrees (hours), minutes, seconds and fractional seconds into decimal degrees (hours). |
| `°→''"` | Convert decimal degrees (hours) into degrees (hours), minutes, seconds and fractional seconds. |

---

## Logic

| Instruction | Description |
|------------|-------------|
| `∧` | Perform a bitwise AND operation on **X** and **Y**. |
| `∨` | Perform a bitwise OR operation on **X** and **Y**. |
| `⊕` | Perform a bitwise XOR operation on **X** and **Y**. |
| `¬` | Perform a bitwise NOT operation on **X**. |

---

## Random Number

| Instruction | Description |
|------------|-------------|
| `СЧ` | Generate a pseudo-random number in the range `[0, 1)` and store it in **X**. |

---

## Memory Access

| Instruction | Description |
|------------|-------------|
| `X→П` | Store the value of **X** into a data register (`RG0`…`RGe`). |
| `П→X` | Load the value of a data register (`RG0`…`RGe`) into **X**. |
| `К X→П` | Store the value of **X** into a data register using indirect addressing. *Not implemented.* |
| `К П→X` | Load a value from a data register using indirect addressing. *Not implemented.* |

---

## Program Control

| Instruction | Description |
|------------|-------------|
| `НОП` | Execute no operation. |
| `С/П` | Stop program execution. |
| `БП` | Jump unconditionally to the specified program address. |
| `ПП` | Call a subroutine at the specified program address. |
| `В/О` | Return from a subroutine. |

---

## Conditional Jumps

| Instruction | Description |
|------------|-------------|
| `X<0` | Jump if **X** is negative. |
| `X≥0` | Jump if **X** is greater than or equal to zero. |
| `X=0` | Jump if **X** is equal to zero. |
| `X≠0` | Jump if **X** is not equal to zero. |

---

## Indirect Program Control

| Instruction | Description |
|------------|-------------|
| `К БП` | Jump to the program address stored in a data register. |
| `К ПП` | Call the subroutine whose address is stored in a data register. |
| `К X<0` | Indirect conditional jump if **X** is negative. |
| `К X≥0` | Indirect conditional jump if **X** is greater than or equal to zero. |
| `К X=0` | Indirect conditional jump if **X** is equal to zero. |
| `К X≠0` | Indirect conditional jump if **X** is not equal to zero. |

---

## Loop Instructions

| Instruction | Description |
|------------|-------------|
| `L0` | Decrement **RG0**. If the result is not zero, jump to the specified address. |
| `L1` | Decrement **RG1**. If the result is not zero, jump to the specified address. |
| `L2` | Decrement **RG2**. If the result is not zero, jump to the specified address. |
| `L3` | Decrement **RG3**. If the result is not zero, jump to the specified address. |

# Memory Architecture

The virtual machine uses the same memory model as the original Elektronika MK-61.

Memory consists of three independent parts:

- a four-level RPN stack (`X`, `Y`, `Z`, `T`);
- the **Last X** register (`X1`);
- fifteen general-purpose data registers (`RG0`…`RGe`).

---

## RPN Stack

The calculator operates on a four-level Reverse Polish Notation (RPN) stack.

```
        Top
    ┌───────┐
    │   T   │
    ├───────┤
    │   Z   │
    ├───────┤
    │   Y   │
    ├───────┤
    │   X   │ ← Current value
    └───────┘
```

Most arithmetic instructions use only the two lowest stack registers.

For example,

```
Before

T = t
Z = z
Y = 8
X = 2

×

After

T = t
Z = z
Y = 8
X = 16
```

The operands are taken from **Y** and **X**, and the result is written back to **X**.

---

## Stack Operations

The virtual machine provides several instructions for manipulating the stack.

### `В↑`

Push the current value of **X** onto the stack.

```
Before

T = 40
Z = 30
Y = 20
X = 10

В↑

After

T = 30
Z = 20
Y = 10
X = 10
```

The previous top value (`T`) is discarded.

---

### `↻`

Rotate the stack.

```
Before

T = 40
Z = 30
Y = 20
X = 10

↻

After

T = 10
Z = 40
Y = 30
X = 20
```

This operation performs a circular rotation of all four stack registers.

---

### `↔`

Exchange the values of **X** and **Y**.

```
Before

Y = 20
X = 10

↔

After

Y = 10
X = 20
```

Only the two lowest stack registers are affected.

---

## Last X Register

The virtual machine stores the previous value of register **X** in a dedicated register called **X1** (Last X).

Many mathematical instructions automatically save the original value of **X** before replacing it with a result.

The `Вx` instruction restores this value.

Example:

```
X = 9

√

X = 3
X1 = 9

Вx

X = 9
```

This feature is useful when an intermediate value must be reused after a calculation.

---

## Data Registers

The virtual machine provides fifteen general-purpose data registers.

```
RG 0
RG 1
RG 2
RG 3
RG 4
RG 5
RG 6
RG 7
RG 8
RG 9
RG a
RG b
RG c
RG d
RG e
```

These registers are independent of the stack.

Unlike the stack registers, their values never change automatically during calculations.

They are typically used to store variables, constants, counters, and intermediate results.

---

## Store to Register — `X→П`

Store the current value of **X** into a data register.

Example

```
X = 42

X→П 5

RG5 = 42
```

The value in **X** is preserved.

---

## Load from Register — `П→X`

Load the value of a data register into **X**.

Example

```
RG 5 = 42

П→X 5

X = 42
```

The register itself is not modified.

---

## Available Registers

Both instructions `X→П` and `П→X` can access any of the fifteen data registers.

| Register | Address |
|----------|---------|
| RG 0 | `0` |
| RG 1 | `1` |
| RG 2 | `2` |
| RG 3 | `3` |
| RG 4 | `4` |
| RG 5 | `5` |
| RG 6 | `6` |
| RG 7 | `7` |
| RG 8 | `8` |
| RG 9 | `9` |
| RG a | `a` |
| RG b | `b` |
| RG c | `c` |
| RG d | `d` |
| RG e | `e` |

Examples

```
X→П a
```

stores **X** into **RGa**.

```
П→X e
```

loads **RGe** into **X**.

# Program Execution

Programs are executed sequentially, starting from the first instruction.

The virtual machine maintains an internal **Program Counter (PC)** that identifies the address of the next instruction to execute.

During normal execution, the following steps are performed repeatedly:

1. Read the instruction at the current program address.
2. Execute the instruction.
3. Advance the Program Counter to the next instruction.

```
        Start
          │
          ▼
    Read instruction
          │
          ▼
 Execute instruction
          │
          ▼
 Advance Program Counter
          │
          ▼
 Next instruction
```

Most instructions execute sequentially and simply advance the Program Counter.

Some instructions modify the execution flow by changing the next program address.

Examples include:

- `БП` — unconditional jump.
- `ПП` — call a subroutine.
- `В/О` — return from a subroutine.
- `X<0`, `X≥0`, `X=0`, `X≠0` — conditional jumps.
- `К БП`, `К ПП` and other indirect variants — indirect program control.

---

## Sequential Execution

The following program adds two values and stores the result.

```
00  П→X 0
01  П→X 1
02  +
03  X→П 2
04  С/П
```

Execution proceeds in address order.

```
00 → 01 → 02 → 03 → 04
```

---

## Unconditional Jump

The `БП` instruction transfers execution to another program address.

```
00  ...
01  БП 05
02  ...
03  ...
04  ...
05  ...
```

Execution order becomes

```
00 → 01 ─────► 05
```

Instructions at addresses `02`–`04` are skipped.

---

## Conditional Jump

Conditional jump instructions evaluate the value currently stored in register **X**.

If the condition is satisfied, execution continues from the specified address.

Otherwise, execution proceeds with the next instruction.

```
00  X=0 05
01  ...
02  ...
03  ...
04  ...
05  ...
```

If `X = 0`

```
00 ─────► 05
```

Otherwise

```
00 → 01 → 02 → ...
```

---

## Subroutine Calls

The `ПП` instruction transfers execution to another program location while remembering the return address.

The `В/О` instruction resumes execution immediately after the corresponding `ПП`.

```
00  ...
01  ПП 10
02  ...
03  С/П

10  ...
11  ...
12  В/О
```

Execution order

```
00
 │
 ▼
01 ─────► 10 → 11 → 12
               │
               ▼
               02 → 03
```

---

## Program Termination

Program execution continues until one of the following occurs:

- the `С/П` instruction is executed;
- the virtual machine reaches the end of the loaded program;
- execution is stopped by the host application.

---
# Indirect Addressing

In addition to direct addressing, the virtual machine supports **indirect addressing**, allowing registers and program addresses to be determined at runtime.

Instead of using the operand as the final register number or program address, indirect instructions first read a value from the specified data register. That value is then interpreted as the actual destination.

This mechanism enables dynamic memory access and dynamic control flow without modifying the program itself.

Indirect addressing is available through the following instructions:

| Instruction | Description |
|-------------|-------------|
| `К П→X` | Load a value from a register whose number is stored in another register. |
| `К X→П` | Store a value into a register whose number is stored in another register. |
| `К БП` | Jump to a program address stored in a register. |
| `К ПП` | Call a subroutine whose address is stored in a register. |
| `К X<0` | Conditional indirect jump if `X < 0`. |
| `К X≥0` | Conditional indirect jump if `X ≥ 0`. |
| `К X=0` | Conditional indirect jump if `X = 0`. |
| `К X≠0` | Conditional indirect jump if `X ≠ 0`. |

---

## Indirect Register Access

The following example stores the register number `7` in **RG2**.

```
RG2 = 7
```

Executing

```
К П→X 2
```

is equivalent to

```
П→X 7
```

because the instruction first reads the value stored in **RG2** and then uses that value as the register number.

Likewise,

```
RG5 = c

К X→П 5
```

behaves as

```
X→П c
```

allowing the destination register to be selected dynamically.

---

## Indirect Program Control

Indirect addressing can also be used for program execution.

Suppose **RG1** contains the value

```
RG1 = 42
```

Executing

```
К БП 1
```

is equivalent to

```
БП 42
```

Execution continues from program address `42`.

Similarly,

```
RG3 = 80

К ПП 3
```

behaves as

```
ПП 80
```

calling the subroutine located at address `80`.

Conditional indirect jumps work in the same way. The jump destination is read from a register instead of being encoded directly in the instruction.

---

# Subroutines

Subroutines allow a sequence of instructions to be reused from multiple locations within a program.

Instead of duplicating the same code, a program can transfer execution to a subroutine, execute its instructions, and then return to the point from which it was called.

The virtual machine provides two instructions for working with subroutines:

| Instruction | Description |
|-------------|-------------|
| `ПП` | Call a subroutine at the specified program address. |
| `В/О` | Return from the current subroutine. |

Indirect subroutine calls are also supported through the `К ПП` instruction.

---

## Calling a Subroutine

The `ПП` instruction transfers execution to another program address while automatically saving the return address.

```
00  ...
01  ПП 10
02  ...
03  С/П

10  ...
11  ...
12  В/О
```

Execution proceeds as follows:

```
00
 │
 ▼
01 ─────► 10 → 11 → 12
               │
               ▼
               02 → 03
```

When `В/О` is executed, program execution resumes with the instruction immediately following the corresponding `ПП`.

---

## Returning from a Subroutine

A subroutine normally ends with the `В/О` instruction.

```
20  ...
21  ...
22  В/О
```

After executing `В/О`, the virtual machine restores the saved return address and continues execution of the calling program.

---

## Nested Subroutine Calls

A subroutine may call another subroutine.

```
00  ПП 20
01  ...

20  ...
21  ПП 40
22  ...
23  В/О

40  ...
41  ...
42  В/О
```

Execution order:

```
00
 │
 ▼
20
 │
 ▼
40 → 41 → 42
 │
 ▼
22 → 23
 │
 ▼
01
```

Each subroutine returns to its own caller, allowing multiple levels of nested execution.

---

## Indirect Subroutine Calls

The `К ПП` instruction performs an indirect subroutine call.

Instead of specifying the destination address directly, the instruction first reads the address from a data register.

For example,

```
RG3 = 80

К ПП 3
```

behaves as

```
ПП 80
```

allowing the called subroutine to be selected dynamically at runtime.

---

# Loop Instructions

The virtual machine provides four dedicated instructions for implementing counted loops.

Each instruction is permanently associated with one of the first four data registers, which acts as the loop counter.

| Instruction | Counter Register |
|-------------|------------------|
| `L0` | `RG0` |
| `L1` | `RG1` |
| `L2` | `RG2` |
| `L3` | `RG3` |

Each loop instruction is immediately followed by a program address that specifies the beginning of the loop body.

When a loop instruction is executed, the virtual machine performs the following operations:

1. Read the associated loop counter.
2. Decrement its value by one.
3. Store the updated value back into the register.
4. Test the updated value for zero.
5. If the value is not zero, execution continues from the specified loop address.
6. Otherwise, the loop terminates and execution continues with the instruction following the loop address.

---

## Loop Counter

Before entering a loop, the corresponding register must contain the required number of iterations.

For example,

```text
RG0 = 4
```

Executing `L0` repeatedly changes the register as follows:

```text
4 → 3 → 2 → 1 → 0
```

The loop continues while the updated counter value is not zero.

---

## Program Structure

A loop instruction occupies two program addresses.

```
04  L0
05  02
```

The second address specifies the first instruction of the loop body.

```
        ┌──────────────┐
        │  Loop Body   │
        └──────┬───────┘
               │
               ▼
              L0
               │
        RG0 = RG0 − 1
               │
       ┌───────┴────────┐
       │                │
   RG0 ≠ 0          RG0 = 0
       │                │
       ▼                ▼
 Jump to 02      Continue execution
```

---

## Simple Example

The following program executes the loop body four times.

```
00  X→П 0
01  ...

02  ...
03  ...

04  L0
05  02

06  С/П
```

Assuming

```
RG0 = 4
```

execution proceeds as

```
02 → 03 → 04
↑         │
└─────────┘

02 → 03 → 04
↑         │
└─────────┘

02 → 03 → 04
↑         │
└─────────┘

02 → 03 → 04
           │
           ▼
          06
```

The loop body is executed exactly four times.

---

## Multiple Loop Counters

Each loop instruction always operates on its own dedicated register.

```
L0 → RG0
L1 → RG1
L2 → RG2
L3 → RG3
```

This allows several independent loop counters to exist simultaneously.

For example,

```
RG0 = 10
RG1 = 5
```

may be used as the counters for two independent loops.

---

## Example

The following program evaluates

```
      4
      Σ (2 · tg(xᵢ / 3) + 4)
     i=1
```

for

```
xᵢ = 1, 2, 3, 4
```

The number of iterations (`4`) is stored in `RG0`, while `RG5` is used to accumulate the partial sums.

| Address | Instruction |
|:------:|-------------|
| 00 | `X→П 0` |
| 01 | `П→X 0` |
| 02 | `3` |
| 03 | `÷` |
| 04 | `tg` |
| 05 | `2` |
| 06 | `×` |
| 07 | `4` |
| 08 | `+` |
| 09 | `П→X 5` |
| 10 | `+` |
| 11 | `X→П 5` |
| 12 | `L0` |
| 13 | `01` |
| 14 | `С/П` |

During each iteration, the program computes

```
2 · tg(xᵢ / 3) + 4
```

and adds the result to the accumulator stored in `RG5`.

When the final iteration is reached, `RG0` contains

```text
1
```

Executing `L0` decrements the counter

```text
1 → 0
```

Since the updated counter is zero, the loop terminates. The jump address (`01`) is skipped, and execution continues with the `С/П` instruction.

After program execution completes, register **X** contains the result

```text
29.644467
```
---
# Examples


## Summation and Product

This program computes either the sum or the product of the first **n** positive integers.

Depending on the arithmetic operation selected in the program, it evaluates either

```
      n
S =   Σ i
     i=1
```

or

```
      n
P =   ∏ i
     i=1
```

---

### Formula

Summation:

```
      n
S =   Σ i
     i=1
```

Product:

```
      n
P =   ∏ i
     i=1
```

---

### Register Allocation

| Register | Purpose |
|----------|---------|
| `RG2` | Running result (sum or product) |
| `RG3` | Current value of `i` |

---

### Program

| Address | Instruction |
|:------:|-------------|
| 00 | `X→П 2` |
| 01 | `1` |
| 02 | `−` |
| 03 | `X→П 3` |
| 04 | `X≠0` |
| 05 | `13` |
| 06 | `В↑` |
| 07 | `П→X 2` |
| 08 | `+` **or** `×` |
| 09 | `X→П 2` |
| 10 | `П→X 3` |
| 11 | `БП` |
| 12 | `01` |
| 13 | `П→X 2` |
| 14 | `С/П` |
| 15 | `БП` |
| 16 | `00` |

Replace the instruction at address **08** with:

- `+` to calculate the sum;
- `×` to calculate the product.

---

### Program Operation

The program maintains two working registers.

- `RG2` stores the current result.
- `RG3` stores the current value of the sequence.

During each iteration the program:

1. Increments the current value.
2. Adds or multiplies it with the accumulated result.
3. Stores the updated result.
4. Repeats until the specified number of terms has been processed.

---

### Example 1 — Summation

Calculate

```
      10
S =   Σ i
     i=1
```

Store

```
10
```

in register **X** before starting the program.

Use the `+` instruction at address **08**.

The program computes

```
1 + 2 + 3 + ... + 10
```

Result:

```
55
```

---

### Example 2 — Product

Calculate

```
      5
P =   ∏ i
     i=1
```

Use the `×` instruction at address **08**.

The program computes

```
1 × 2 × 3 × 4 × 5
```

Result:

```
120
```

---
## Arithmetic Mean

This program calculates the arithmetic mean of a statistical data set.

For a sequence of values

```text
x₁, x₂, ..., xₙ
```

the arithmetic mean is

```text
        1   n
X̄ = ────── Σ xᵢ
        n  i=1
```

The values are entered one at a time. After each value is entered, the program updates both the accumulated sum and the number of processed values.

---

### Register Allocation

| Register | Purpose |
|----------|---------|
| `RG2` | Accumulated sum |
| `RG3` | Number of processed values |

---

### Program

| Address | Instruction |
|:------:|-------------|
| 00 | `X→П 2` |
| 01 | `1` |
| 02 | `X→П 3` |
| 03 | `П→X 2` |
| 04 | `С/П` |
| 05 | `В↑` |
| 06 | `П→X 2` |
| 07 | `+` |
| 08 | `X→П 2` |
| 09 | `П→X 3` |
| 10 | `1` |
| 11 | `+` |
| 12 | `X→П 3` |
| 13 | `В↑` |
| 14 | `П→X 2` |
| 15 | `↔` |
| 16 | `÷` |
| 17 | `С/П` |
| 18 | `БП` |
| 19 | `05` |

---

### Initialization

Enter the first value of the data set and start execution at address **00**.

The program:

1. stores the first value in `RG2`;
2. initializes the counter in `RG3` to `1`;
3. displays the current mean;
4. stops at address **04** and waits for the next value.

At this point, the arithmetic mean is equal to the first entered value.

---

### Adding Another Value

After the program stops, enter the next value and resume execution from address **05**.

The program then:

1. adds the new value to the accumulated sum in `RG2`;
2. increments the number of processed values in `RG3`;
3. divides the accumulated sum by the number of values;
4. displays the updated arithmetic mean;
5. stops at address **17**.

When execution is resumed, `БП 05` returns to the input-processing section so that another value can be entered.

---

### Example

Calculate the arithmetic mean of

```text
4, 7, 9, 10
```

The program updates its registers as follows:

| Entered Value | `RG2` — Sum | `RG3` — Count | Displayed Mean |
|--------------:|------------:|--------------:|---------------:|
| `4` | `4` | `1` | `4` |
| `7` | `11` | `2` | `5.5` |
| `9` | `20` | `3` | `6.6666667` |
| `10` | `30` | `4` | `7.5` |

The final result is

```text
7.5
```

---

## Projectile Range

This program calculates the horizontal range of a projectile launched with an initial velocity at a specified angle, neglecting air resistance.

The calculation is based on the classical projectile motion equation.

---

### Formula

The projectile range is given by

```text
          V² · sin(2α)
S = ─────────────────────
              g
```

where

| Parameter | Description |
|-----------|-------------|
| `S` | Horizontal range |
| `V` | Initial velocity |
| `α` | Launch angle |
| `g` | Gravitational acceleration |

For Earth,

```text
g = 9.80665 m/s²
```

---

### Register Allocation

| Register | Purpose |
|----------|---------|
| `RG2` | Initial velocity `V` |
| `RG3` | Launch angle `α` |
| `RG4` | Gravitational acceleration `g` |

---

### Program

| Address | Instruction |
|:------:|-------------|
| 00 | `П→X 2` |
| 01 | `В↑` |
| 02 | `×` |
| 03 | `П→X 3` |
| 04 | `2` |
| 05 | `×` |
| 06 | `F SIN` |
| 07 | `×` |
| 08 | `П→X 4` |
| 09 | `÷` |
| 10 | `С/П` |

---

### Initialization

Before starting the program, store the input values in the corresponding registers.

| Register | Value |
|----------|------:|
| `RG2` | Initial velocity |
| `RG3` | Launch angle (degrees) |
| `RG4` | `9.80665` |

Start execution from address **00**.

After execution completes, register **X** contains the calculated horizontal range.

---

### Example

A ball is launched with

- Initial velocity: **40 m/s**
- Launch angle: **45°**

Using

```text
g = 9.80665 m/s²
```

the program evaluates

```text
      40² · sin(90°)
S = ─────────────────
          9.80665
```

Since

```text
sin(90°) = 1
```

the result is

```text
163.15 m
```

which represents the maximum horizontal distance traveled by the projectile.

---

## Compound Interest

This program computes one of the four parameters that describe compound growth when the other three are known.

The program can calculate:

- the number of compounding periods (`n`);
- the interest rate per period (`i`);
- the initial investment (`H`);
- the final investment (`K`).

---

### Formula

The following equations are used:

```
          ln(K / H)
n = ─────────────────────
     ln(1 + i / 100)
```

```
            n
i = ((K / H)    − 1) × 100
```

```
              −n
H = K(1 + i/100)
```

```
             n
K = H(1 + i/100)
```

where

| Parameter | Description |
|-----------|-------------|
| `n` | Number of compounding periods |
| `i` | Interest rate per period (%) |
| `H` | Initial investment |
| `K` | Final investment |

---

### Register Allocation

| Register | Purpose |
|----------|---------|
| `RG2` | `n` |
| `RG3` | `i` |
| `RG4` | `H` |
| `RG5` | `K` |
| `RG6` | Intermediate calculations |

The frequently used expression

```
1 + i / 100
```

is implemented as a reusable subroutine beginning at address **47**.

---

### Program Layout

The program consists of four independent calculation routines.

| Address Range | Calculates |
|---------------|------------|
| `00–12` | `n` |
| `13–29` | `i` |
| `30–38` | `H` |
| `39–46` | `K` |
| `47–57` | Subroutine: `1 + i / 100` |

---

### Program

| Address | Instruction | Address | Instruction | Address | Instruction |
|:------:|-------------|:------:|-------------|:------:|-------------|
| 00 | `ПП 47` | 15 | `П→X 4` | 30 | `ПП 47` |
| 01 | `F ln` | 16 | `÷` | 31 | `F xʸ` |
| 02 | `X→П 6` | 17 | `В↑` | 32 | `F 1/x` |
| 03 | `П→X 5` | 18 | `П→X 2` | 33 | `В↑` |
| 04 | `В↑` | 19 | `F 1/x` | 34 | `П→X 5` |
| 05 | `П→X 4` | 20 | `↔` | 35 | `×` |
| 06 | `÷` | 21 | `F xʸ` | 36 | `X→П 4` |
| 07 | `F ln` | 22 | `100` | 37 | `С/П` |
| 08 | `П→X 6` | 23 | `−` | 38 | |
| 09 | `÷` | 24 | `100` | 39 | `ПП 47` |
| 10 | `X→П 2` | 25 | `×` | 40 | `F xʸ` |
| 11 | `С/П` | 26 | `X→П 3` | 41 | `В↑` |
| 12 | | 27 | `С/П` | 42 | `П→X 4` |
| 13 | `П→X 5` | 28 | | 43 | `×` |
| 14 | `В↑` | 29 | | 44 | `X→П 5` |
| | | | | 45 | `С/П` |

---

### Subroutine

The subroutine beginning at address **47** computes

```
1 + i / 100
```

and is shared by all four calculation routines.

| Address | Instruction |
|:------:|-------------|
| 47 | `П→X 3` |
| 48 | `100` |
| 49 | `÷` |
| 50 | `1` |
| 51 | `+` |
| 52 | `В↑` |
| 53 | `П→X 2` |
| 54 | `↔` |
| 55 | `В/О` |

---

### Example

Suppose an initial investment of **$270 million** grows at an annual reinvestment rate of **7.5%**.

Given

| Parameter | Value |
|-----------|------:|
| `n` | `6` |
| `i` | `7.5` |
| `H` | `$270 million` |
| `K` | Unknown |

Store the known values in their corresponding registers:

| Register | Value |
|----------|------:|
| `RG2` | `6` |
| `RG3` | `7.5` |
| `RG4` | `270` |

To calculate the final investment (`K`), start execution at address **39**.

After program execution completes, register **X** contains

```
416.6914
```

which corresponds to a final investment of **$416.6914 million**.