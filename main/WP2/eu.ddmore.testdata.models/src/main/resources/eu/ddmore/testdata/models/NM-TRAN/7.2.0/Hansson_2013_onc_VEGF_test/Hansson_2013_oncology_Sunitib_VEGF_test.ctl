; Script generated by the pharmML2Nmtran Converter v.1.0.0
; Source	: PharmML 0.3.1
; Target	: NMTRAN 7.2.0
; Model 	: Hansson_2013_oncology_Sunitib_VEGF_test
; Dated 	: Wed Feb 11 14:01:36 GMT 2015

$PROBLEM "Hansson_2013_oncology_Sunitib_VEGF_test - generated by MDL2PharmML v.6.0"

$INPUT  ID CYCL TIME DAYS FLAG DVX DV DOS PLA CL EVID
$DATA BIOMARKER_exDATA_VEGF.csv IGNORE=#
$SUBS ADVAN13 TOL=9

$MODEL
COMP (VEGF)

$PK
POP_IC50 = THETA(1)
POP_BM0 = THETA(2)
POP_MRT = THETA(3)
POP_HILL = THETA(4)
POP_TVSLP = THETA(5)
POP_RES_VEGF_ADD = THETA(6)
POP_IMAX = THETA(7)

eta_BM0 = ETA(1)
eta_IC50 = ETA(2)
eta_MRT_VEGFs = ETA(3)
eta_TVSLP = ETA(4)

MU_1 = LOG(POP_BM0);
BM0 = EXP(MU_1 + ETA(1));


IMAX1 =  POP_IMAX ;

MU_2 = LOG(POP_IC50);
IC50 = EXP(MU_2 + ETA(2));


HILL =  POP_HILL ;

MU_3 = LOG(POP_MRT);
MRT1 = EXP(MU_3 + ETA(3));


TVSLP =  (POP_TVSLP/1000) ;

MU_4 = LOG(TVSLP);
DPSLP = EXP(MU_4 + ETA(4));


KOUT =  (1/MRT1) ;

A_0(1) = BM0

$DES
VEGF = A(1)

AUC =  (DOS/CL)
DP1 =  (BM0*(1+(DPSLP*T)))
KIN =  (DP1*KOUT)
EFF =  ((IMAX1*(AUC**HILL))/((IC50**HILL)+(AUC**HILL)))
DADT(1) = (KIN-((KOUT*(1-EFF))*VEGF))

$ERROR
IPRED = LOGVEGF
W = POP_RES_VEGF_ADD
Y = IPRED+W*EPS(1)
IRES = DV - IPRED
IWRES = IRES/W

$THETA
( 0.0 , 1.0 )	;POP_IC50
( 0.0 , 59.7 )	;POP_BM0
( 0.0 , 91.0 )	;POP_MRT
( 0.0 , 3.31 )	;POP_HILL
( -0.06 , 0.035 )	;POP_TVSLP
( 0.0 , 0.445 )	;POP_RES_VEGF_ADD
( 1.0  FIX )	;POP_IMAX

$OMEGA
( 0.252 )	;OMEGA_BM0
( 0.06 )	;OMEGA_MRT_VEGFs
( 0.253 )	;OMEGA_IC50
( 2.95 )	;OMEGA_TVSLP

$SIGMA
1.0 FIX	;SIGMA_RES_W


$EST METHOD=COND MAXEVALS=9999 PRINT=10 NOABORT
$COV

$TABLE  ID TIME CYCL DAYS FLAG DVX DOS PLA CL EVID PRED IPRED RES IRES WRES IWRES Y DV NOAPPEND NOPRINT FILE=sdtab

$TABLE  ID BM0 IMAX1 IC50 HILL MRT1 TVSLP DPSLP KOUT ETA_BM0 ETA_IC50 ETA_MRT_VEGFS ETA_TVSLP NOAPPEND NOPRINT FILE=patab

$TABLE  ID CL NOAPPEND NOPRINT FILE=cotab


