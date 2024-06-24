//R2 = R0 * R1

@i
M=0 // M=0

@ R2
M = 0

( LOOP )
@i
D=M
@R0
D = D-M
@END
D;JGE
@R1
D=M
@R2
M=D+M
@i
M=1+M
@LOOP
0 ; JMP

(END)
@END
0;JMP

