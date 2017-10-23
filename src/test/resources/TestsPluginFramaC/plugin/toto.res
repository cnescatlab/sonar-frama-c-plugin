[kernel] preprocessing with "gcc -C -E -I.  memexec.c"
[metrics] Defined functions (19)
          ======================
           f11 (7 calls); f1 (1 call); f2 (1 call); f3 (1 call); fbug (2 calls);
           bug (1 call); f4_11 (1 call); f4_12 (1 call); f4_2 (2 calls); f4 (1 call);
           f5_aux (2 calls); f5 (1 call); f6_1 (2 calls); f6 (1 call); f7_1 (2 calls);
           f7 (1 call); f8_1 (3 calls); f8 (1 call); main (0 call); 
          
          Undefined functions (0)
          =======================
           
          
          Potential entry points (1)
          ==========================
           main; 
          
          Global metrics
          ============== 
          Sloc = 94
          Decision point = 7
          Global variables = 12
          If = 7
          Loop = 1
          Goto = 0
          Assignment = 30
          Exit point = 19
          Function = 19
          Function call = 33
          Pointer dereferencing = 8
          Cyclomatic complexity = -10
[value] Analyzing a complete application starting at main
[value] Computing initial state
[value] Initial state computed
[value] Values of globals at initialization
  x1 ∈ {0}
  y1 ∈ {0}
  z1 ∈ {0}
  c ∈ [--..--]
  p ∈ {0}
  i ∈ {0}
  t[0..9] ∈ {0}
  ps ∈ {0}
  S[0..9] ∈ {0}
  g_f5_1 ∈ {0}
  g_f5_2 ∈ {0}
  two_fields ∈ {0}
[value] computing for function f1 <- main.
        Called from memexec.c:154.
[value] computing for function f11 <- f1 <- main.
        Called from memexec.c:12.
[value] Recording results for f11
[value] Done for function f11
[value] computing for function f11 <- f1 <- main.
        Called from memexec.c:13.
[value] Recording results for f11
[value] Done for function f11
[value] computing for function f11 <- f1 <- main.
        Called from memexec.c:14.
[value] Recording results for f11
[value] Done for function f11
[value] computing for function f11 <- f1 <- main.
        Called from memexec.c:16.
[value] Recording results for f11
[value] Done for function f11
[value] computing for function f11 <- f1 <- main.
        Called from memexec.c:18.
[value] Recording results for f11
[value] Done for function f11
[value] computing for function f11 <- f1 <- main.
        Called from memexec.c:20.
[value] Recording results for f11
[value] Done for function f11
[value] computing for function f11 <- f1 <- main.
        Called from memexec.c:21.
[value] Recording results for f11
[value] Done for function f11
[value] Recording results for f1
[value] Done for function f1
[value] computing for function f2 <- main.
        Called from memexec.c:155.
[value] Recording results for f2
[value] Done for function f2
[value] computing for function f3 <- main.
        Called from memexec.c:156.
[value] Recording results for f3
[value] Done for function f3
[value] computing for function bug <- main.
        Called from memexec.c:157.
[value] computing for function fbug <- bug <- main.
        Called from memexec.c:40.
memexec.c:33:[kernel] warning: out of bounds read. assert \valid_read(p);
[value] Recording results for fbug
[value] Done for function fbug
[value] computing for function fbug <- bug <- main.
        Called from memexec.c:42.
[value] Recording results for fbug
[value] Done for function fbug
memexec.c:43:[value] warning: locals {x} escaping the scope of bug through p
[value] Recording results for bug
[value] Done for function bug
[value] computing for function f4 <- main.
        Called from memexec.c:158.
[value] computing for function f4_2 <- f4 <- main.
        Called from memexec.c:84.
[value] computing for function f4_11 <- f4_2 <- f4 <- main.
        Called from memexec.c:74.
memexec.c:59:[kernel] warning: accessing out of bounds index {12}. assert ps->i < 10;
memexec.c:59:[kernel] warning: all target addresses were invalid. This path is assumed to be dead.
[value] Recording results for f4_11
[value] Done for function f4_11
[value] computing for function f4_12 <- f4_2 <- f4 <- main.
        Called from memexec.c:76.
memexec.c:63:[kernel] warning: accessing out of bounds index {11}. assert i < 10;
memexec.c:63:[kernel] warning: all target addresses were invalid. This path is assumed to be dead.
[value] Recording results for f4_12
[value] Done for function f4_12
[value] Recording results for f4_2
[value] Done for function f4_2
[value] computing for function f4_2 <- f4 <- main.
        Called from memexec.c:87.
[value] computing for function f4_11 <- f4_2 <- f4 <- main.
        Called from memexec.c:74.
[value] Recording results for f4_11
[value] Done for function f4_11
[value] computing for function f4_12 <- f4_2 <- f4 <- main.
        Called from memexec.c:76.
[value] Recording results for f4_12
[value] Done for function f4_12
[value] Recording results for f4_2
[value] Done for function f4_2
[value] Recording results for f4
[value] Done for function f4
[value] computing for function f5 <- main.
        Called from memexec.c:159.
memexec.c:104:[value] assigning non deterministic value for the first time
[value] computing for function f5_aux <- f5 <- main.
        Called from memexec.c:107.
memexec.c:94:[value] Assertion got status unknown.
memexec.c:96:[value] Assertion got status unknown.
memexec.c:98:[value] entering loop for the first time
[value] Recording results for f5_aux
[value] Done for function f5_aux
[value] Called Frama_C_show_each_f5([-2147483648..2147483647],
                                    [-2147483648..6],
                                    [-2147483648..7])
[value] computing for function f5_aux <- f5 <- main.
        Called from memexec.c:113.
[value] Recording results for f5_aux
[value] Done for function f5_aux
[value] Called Frama_C_show_each_f5([-2147483648..2147483647],
                                    [-2147483648..6],
                                    [-2147483648..7])
[value] Recording results for f5
[value] Done for function f5
[value] computing for function f6 <- main.
        Called from memexec.c:160.
[value] computing for function f6_1 <- f6 <- main.
        Called from memexec.c:123.
[value] Recording results for f6_1
[value] Done for function f6_1
[value] computing for function f6_1 <- f6 <- main.
        Called from memexec.c:126.
[value] Recording results for f6_1
[value] Done for function f6_1
[value] Recording results for f6
[value] Done for function f6
[value] computing for function f7 <- main.
        Called from memexec.c:161.
[value] computing for function f7_1 <- f7 <- main.
        Called from memexec.c:136.
[value] Recording results for f7_1
[value] Done for function f7_1
[value] computing for function f7_1 <- f7 <- main.
        Called from memexec.c:137.
[value] Recording results for f7_1
[value] Done for function f7_1
[value] Recording results for f7
[value] Done for function f7
[value] computing for function f8 <- main.
        Called from memexec.c:162.
[value] computing for function f8_1 <- f8 <- main.
        Called from memexec.c:147.
memexec.c:141:[kernel] warning: accessing uninitialized left-value: assert \initialized(q);
memexec.c:141:[kernel] warning: completely indeterminate value in x.
[value] Recording results for f8_1
[value] Done for function f8_1
[value] computing for function f8_1 <- f8 <- main.
        Called from memexec.c:149.
[value] Recording results for f8_1
[value] Done for function f8_1
[value] computing for function f8_1 <- f8 <- main.
        Called from memexec.c:150.
[value] Recording results for f8_1
[value] Done for function f8_1
[value] Recording results for f8
[value] Done for function f8
[value] Recording results for main
[value] done for function main
[value] ====== VALUES COMPUTED ======
[value] Values at end of function f11:
  x1 ∈ {1}
[value] Values at end of function f1:
  x1 ∈ {1}
[value] Values at end of function f2:
  
[value] Values at end of function f3:
  
[value] Values at end of function f4_11:
  t[0..5] ∈ {0}
   [6] ∈ {1}
   [7..9] ∈ {0}
[value] Values at end of function f4_12:
  t[0..1] ∈ {0}
   [2] ∈ {3}
   [3..4] ∈ {0}
   [5] ∈ {2}
   [6..9] ∈ {0}
[value] Values at end of function f4_2:
  i ∈ {5}
  t[0..1] ∈ {0}
   [2] ∈ {0; 3}
   [3..4] ∈ {0}
   [5] ∈ {0; 2}
   [6] ∈ {0; 1}
   [7..9] ∈ {0}
  ps ∈ {{ &S + {32} }}
  S[0..7] ∈ {0}
   [8].i ∈ {6}
   [9] ∈ {0}
[value] Values at end of function f4:
  i ∈ {0; 5}
  t[0..1] ∈ {0}
   [2] ∈ {0; 3}
   [3..4] ∈ {0}
   [5] ∈ {0; 2}
   [6] ∈ {0; 1}
   [7..9] ∈ {0}
  ps ∈ {{ NULL ; &S + {32} }}
  S[0..7] ∈ {0}
   [8].i ∈ {0; 6}
   [9] ∈ {0}
  n ∈ {6; 12}
[value] Values at end of function f5_aux:
  v ∈ [--..--]
[value] Values at end of function f5:
  g_f5_1 ∈ [-2147483648..6]
  g_f5_2 ∈ [-2147483648..7]
  arg ∈ [--..--]
[value] Values at end of function f6_1:
  two_fields.x ∈ {1}
            .y ∈ {2; 3}
[value] Values at end of function f6:
  two_fields.x ∈ {1}
            .y ∈ {3}
[value] Values at end of function f7_1:
  x{.x; .y} ∈ {1}
[value] Values at end of function f7:
  x{.x; .y} ∈ {1}
[value] Values at end of function f8_1:
  q ∈ {0}
[value] Values at end of function f8:
  x ∈ {1}
[value] Values at end of function fbug:
  __retres ∈ {1}
[value] Values at end of function bug:
  p ∈ {{ &x }}
  x ∈ {1}
[value] Values at end of function main:
  x1 ∈ {1}
  p ∈ ESCAPINGADDR
  i ∈ {0; 5}
  t[0..1] ∈ {0}
   [2] ∈ {0; 3}
   [3..4] ∈ {0}
   [5] ∈ {0; 2}
   [6] ∈ {0; 1}
   [7..9] ∈ {0}
  ps ∈ {{ NULL ; &S + {32} }}
  S[0..7] ∈ {0}
   [8].i ∈ {0; 6}
   [9] ∈ {0}
  g_f5_1 ∈ [-2147483648..6]
  g_f5_2 ∈ [-2147483648..7]
  two_fields.x ∈ {1}
            .y ∈ {3}
