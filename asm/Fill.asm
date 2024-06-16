// 按下任意健，屏幕会变黑

(LOOP)
@i
M=0
@KBD
D=M
@DRAE
D;JEQ
@i
M=!M

(DRAE)
@8192
D=A
@j
M=D
@k
M=0
@SCREEN
D=A
@address
M=D
(LOOP2)
@k
D=M
@j
D=D-M
@END
D;JEQ
@i
D=M
@address
A=M
M=D
@k
M=M+1
@address
M=M+1
@LOOP2
0;JMP

(END)
@LOOP
0;JMP