/*
 * KOTLIN PSI SPEC TEST (POSITIVE)
 *
 * SPEC VERSION: 0.1-draft
 * PLACES: constant-literals, real-literals -> paragraph 3 -> sentence 1
 * NUMBER: 2
 * DESCRIPTION: Real literals with omitted a whole-number part and an exponent mark.
 */

val value = .0e0
val value = .0e-00
val value = .0E000
val value = .0E+0000

val value = .0E+1
val value = .00e22
val value = .000e-333
val value = .0000e4444
val value = .0E-55555
val value = .00e666666
val value = .000E7777777
val value = .0000e-88888888
val value = .0E+999999999

val value = .1234567890E999999999
val value = .23456789E+123456789
val value = .345678e00000000001
val value = .4567E-100000000000
val value = .56e-0
val value = .65e000000000000
val value = .7654E+010
val value = .876543E1
val value = .98765432e-2
val value = .0987654321E-3

val value = .888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888e+111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111
val value = .000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000e0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000
val value = .000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000e-000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000
val value = .000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000e+000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000
