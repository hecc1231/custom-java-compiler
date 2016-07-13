.386
.model  flat, stdcall
option  casemap:none
includelib \masm32\lib\msvcrt.lib
.data
T4  dd 0
b  dd 0
c  dd 0
a  dd 0
m  dd 0
T1  dd 0
T3  dd 0
T2  dd 0
.stack
.code
start:
(1):
CMP a, 30
JG (2)
JB (3)
(2):
MOV c, 200
MOV EAX, c
MOV EBX, b
MUL EBX
MOV T2, EAX
MOV EAX, T2
ADD EAX, 10
MOV T3, EAX
MOV EAX, T3
MOV a, EAX
MOV EAX, a
ADD EAX, 1
MOV T4, EAX
MOV EAX, T4
MOV m, EAX
JUMP (1)
(3)
end start
