# 词法分析

<img src="https://github.com/1171800323/Compiler/blob/master/pic/image-20200604083038592.png" alt="image-20200604084211204" style="zoom:80%;" />

<img src="/image-20200604083721784.png" alt="image-20200604083721784" style="zoom:80%;" />

<img src="/image-20200604083917425.png" alt="image-20200604083917425" style="zoom:80%;" />

<img src="/image-20200604084456456.png" alt="image-20200604084456456" style="zoom:80%;" />



<img src="/image-20200604083058614.png" alt="image-20200604083058614" style="zoom: 80%;" />

<img src="/image-20200604083127419.png" alt="image-20200604083127419" style="zoom: 80%;" />



# 语法分析

<img src="/image-20200604084236850.png" alt="image-20200604084236850" style="zoom:80%;" />

﻿Program -> P
P -> D P 丨 S P 丨 ε
D -> T id A ; 丨 struct id { P } 丨 proc X id ( M ) { P }
T -> X C
X -> int 丨 float 丨 bool 丨 char
C -> [ num ] C 丨 ε
A -> = F A 丨 , id A 丨 ε
M -> M , X id 丨 X id
S -> L = E ; 丨 if ( B ) then S else S 丨 do S while ( B ) ; 丨 call id ( Elist ) ; 丨 return E ;
L -> L [ num ] 丨 id
E -> E + G 丨 G
G -> G * F 丨 F
F -> ( E ) 丨 num 丨 id 丨 real 丨 character
B -> B || H 丨 H
H -> H && I 丨 I
I -> ! I 丨 ( B ) 丨 E relop E 丨 true 丨 false
relop -> < 丨 > 丨 <= 丨 >= 丨 != 丨 ==
Elist -> Elist , E 丨 E

<img src="/image-20200604083414135.png" alt="image-20200604083414135" style="zoom:80%;" />

<img src="/image-20200604083451427.png" alt="image-20200604083451427" style="zoom:80%;" />

# 语义分析

<img src="/image-20200604084255674.png" alt="image-20200604084255674" style="zoom:80%;" />

Program -> P
P -> D P 丨 S P 丨 ε
D -> T id ; 丨 struct id DM1 { P } 丨 proc X id DM2 ( M ) { P }
DM1 -> ε
DM2 -> ε
T -> X TM C
TM -> ε
X -> int 丨 float 丨 char
C -> [ num ] C 丨 ε
M -> M , X id 丨 X id
S -> L = E ; 丨 id = E ; 丨 if ( B ) BM then S N else BM S 丨 while BM ( B ) do BM S 丨 call id ( Elist ) ; 丨 return E ;
BM -> ε
N -> ε
L -> L [ E ] 丨 id [ E ]
E -> E + G 丨 G
G -> G * F 丨 F
F -> ( E ) 丨 num 丨 id 丨 real 丨 character 丨 L
B -> B || BM H 丨 H
H -> H && BM I 丨 I
I -> ! I 丨 ( B ) 丨 E relop E 丨 true 丨 false
relop -> < 丨 > 丨 <= 丨 >= 丨 != 丨 ==
Elist -> Elist , E 丨 E

<img src="/image-20200604083526500.png" alt="image-20200604083526500" style="zoom:80%;" />



