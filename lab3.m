A = [-0.12 0; 5 0];
B = [2.25; 0]
C = [0 1]
D = 0
h = 0.05
[Phi, Gamma] = c2d(A,B,h)
L = place(Phi,Gamma,[0.8+0.1i, 0.8-0.1i])
Gammae = [Gamma; 0]
Phie = [Phi Gamma; zeros(1,2) 1];
Ce=[C 0]
Ke = acker(Phie', Ce', [0.6+0.2i; 0.6-0.2i; 0.55])'
Le = [L 1];
Hc = ss(Phie-Gammae*Le-Ke*Ce,Ke,Le,0,h);
Hp = ss(Phi,Gamma,C,0,h)

bode(Hc)
figure
margin(Hp*Hc)