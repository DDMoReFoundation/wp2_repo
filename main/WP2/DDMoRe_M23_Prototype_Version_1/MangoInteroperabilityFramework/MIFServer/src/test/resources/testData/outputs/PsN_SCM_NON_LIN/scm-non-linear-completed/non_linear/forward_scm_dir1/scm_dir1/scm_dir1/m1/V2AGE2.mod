;Model Desc: MK-0634 PopPK Base Model, 2-compartment, with exponential IIV on CL and V2
;Project Name: MK-0634 Population PK Analysis
;Project ID: NO PROJECT DESCRIPTION
$PROBLEM    RUN# base_run1
$INPUT      ID PERD PROT TIME AMT DV EVID MDV ADDL II FED AGE
            SEXF=DROP WT=DROP HT=DROP RACE BMI BSA=DROP CRCL=DROP DOSE
$DATA      
            /net/navtst-nonmem/usr/global/mrogalski/other/scm-non-linear/scm-non-linear/non_linear/forward_scm_dir1/scm_dir1/scm_dir1/m1/V2AGE2_data_1_copy.dta
            IGNORE=@
$SUBROUTINE ADVAN4 TRANS4
$OMEGA  DIAGONAL(5)
 0.0266157  ; [P] INTERIND VAR IN CL
 1.83729E-08  ; [P] INTERIND VAR IN V2
 0.0550678  ; [P] INTERIND VAR IN KA
 0  FIX  ; [P] INTERIND VAR IN Q
 0  FIX  ; [P] INTERIND VAR IN V3
$PK

;;; V2AGE-DEFINITION START
V2AGE = ( 1 + THETA(11)*(AGE - 54.00))
;;; V2AGE-DEFINITION END


;;; V2BMI-DEFINITION START
V2BMI = ( 1 + THETA(10)*(BMI - 26.73))
;;; V2BMI-DEFINITION END

;;; V2-RELATION START
V2COV=V2BMI*V2AGE
;;; V2-RELATION END


;;; CLRACE-DEFINITION START
IF(RACE.EQ.0) CLRACE = 1  ; Most common
IF(RACE.EQ.3) CLRACE = ( 1 + THETA(8))
IF(RACE.EQ.1) CLRACE = ( 1 + THETA(9))
;;; CLRACE-DEFINITION END


;;; CLBMI-DEFINITION START
CLBMI = ( 1 + THETA(7)*(BMI - 26.73))
;;; CLBMI-DEFINITION END

;;; CL-RELATION START
CLCOV=CLBMI*CLRACE
;;; CL-RELATION END


; Insert stratification variable (STRT) variable for VPC
; Typically stratify on dose, dosing regimen, protocol, ....
; For no stratification, set STRT=Constant
  STRT=PROT*1000+DOSE

  TVCL=EXP(THETA(1))  

TVCL = CLCOV*TVCL
  MU_1=LOG(TVCL)
  CL=EXP(MU_1+ETA(1))
  
  TVV2=EXP(THETA(2))

TVV2 = V2COV*TVV2
  MU_2=LOG(TVV2)
  V2=EXP(MU_2+ETA(2))

  TVKA=THETA(3)
  MU_3=LOG(TVKA)
  KA=EXP(MU_3+ETA(3))
  
  TVF1=THETA(4)
  F1=TVF1

  TVQ=EXP(THETA(5))
  MU_4=LOG(TVQ)
  Q=EXP(MU_4+ETA(4))
  
  TVV3=EXP(THETA(6))
  MU_5=LOG(TVV3)
  V3=EXP(MU_5+ETA(5))
  
  S2=V2*624.709/1E6

$ERROR

  IPRED=F
  IRES=DV-IPRED

  DEL=0
  IF (F.EQ.0) DEL=10
  W=F
  IWRES=IRES/(W+DEL)
  Y=F*(1+ERR(1)) 

$THETA  6.1557 ; 1 [CL]
 7.36041 ; 2 [V2]
 (0,0.214089) ; 3 [KA]
 1 FIX ; 4 [F1]
 5.85428 ; 5[Q]
 9.3373 ; 6 [V3]
$THETA  (-0.166,0.0231185,0.185) ; CLBMI1
$THETA  (-1,-0.248007,5) ; CLRACE1
 (-1,-0.160143,5) ; CLRACE2
$THETA  (-0.166,-0.0465708,0.185) ; V2BMI1
$THETA  (-0.055,2.9E-05,0.029) ; V2AGE1
$SIGMA  0.121226  ; [P] Variance for proportional error
$ESTIMATION METHOD=1 INTERACTION PRINT=1 NOABORT MAXEVAL=9999
;$COV PRINT=E

;Tables for Xpose Diagnostic Plots

;Standard Table file containing ID, IDV, DV, PRED, IPRED,

; STRT, WRES, IWRES, RES, IRES, etc.
$TABLE      ID TIME IPRED EVID MDV CWRES IWRES STRT NOPRINT ONEHEADER
            FILE=/net/navtst-nonmem/usr/global/mrogalski/other/scm-non-linear/scm-non-linear/non_linear/forward_scm_dir1/scm_dir1/scm_dir1//m1/V2AGE2.mod.sdtab1
;Parameter table containing model parameters - THETAS, ETAS, EPS
$TABLE      ID CL V2 KA F1 Q V3 ETA1 ETA2 ETA3 ETA4 NOPRINT ONEHEADER
            FILE=/net/navtst-nonmem/usr/global/mrogalski/other/scm-non-linear/scm-non-linear/non_linear/forward_scm_dir1/scm_dir1/scm_dir1//m1/V2AGE2.mod.patab1
;Continuous Covariate table - WT, AGE,...

;$TABLE ID WT HT AGE BMI BSA CRCL NOPRINT ONEHEADER FILE=cotab1

;Categorical covariate table - SEX, RACE,.....

;$TABLE ID PROT SEXF RACE FED NOPRINT ONEHEADER FILE=catab1

