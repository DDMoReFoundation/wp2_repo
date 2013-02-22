      MODULE NMPRD4P
      USE SIZES, ONLY: DPSIZE
      USE NMPRD4,ONLY: VRBL
      IMPLICIT NONE
      SAVE
      REAL(KIND=DPSIZE), DIMENSION (:),POINTER ::COM
      REAL(KIND=DPSIZE), POINTER ::CLAGE,CLCOV,STRT,TVCL,MU_1,CL,TVV2
      REAL(KIND=DPSIZE), POINTER ::MU_2,V2,TVKA,MU_3,KA,TVF1,F1,TVQ
      REAL(KIND=DPSIZE), POINTER ::MU_4,Q,TVV3,MU_5,V3,S2,IPRED,IRES
      REAL(KIND=DPSIZE), POINTER ::DEL,W,IWRES,Y,A00073,A00075,A00078
      REAL(KIND=DPSIZE), POINTER ::A00080,A00083,A00085,A00088,A00090
      REAL(KIND=DPSIZE), POINTER ::A00093,A00095,A00096,A00097,D00111
      REAL(KIND=DPSIZE), POINTER ::D00112,D00113,D00114,D00115,D00165
      REAL(KIND=DPSIZE), POINTER ::D00164,D00163,D00162,D00161,C00072
      REAL(KIND=DPSIZE), POINTER ::D00300,D00299,D00298,D00297,D00296
      CONTAINS
      SUBROUTINE ASSOCNMPRD4
      COM=>VRBL
      CLAGE=>COM(00001);CLCOV=>COM(00002);STRT=>COM(00003)
      TVCL=>COM(00004);MU_1=>COM(00005);CL=>COM(00006)
      TVV2=>COM(00007);MU_2=>COM(00008);V2=>COM(00009)
      TVKA=>COM(00010);MU_3=>COM(00011);KA=>COM(00012)
      TVF1=>COM(00013);F1=>COM(00014);TVQ=>COM(00015);MU_4=>COM(00016)
      Q=>COM(00017);TVV3=>COM(00018);MU_5=>COM(00019);V3=>COM(00020)
      S2=>COM(00021);IPRED=>COM(00022);IRES=>COM(00023)
      DEL=>COM(00024);W=>COM(00025);IWRES=>COM(00026);Y=>COM(00027)
      A00073=>COM(00028);A00075=>COM(00029);A00078=>COM(00030)
      A00080=>COM(00031);A00083=>COM(00032);A00085=>COM(00033)
      A00088=>COM(00034);A00090=>COM(00035);A00093=>COM(00036)
      A00095=>COM(00037);A00096=>COM(00038);A00097=>COM(00039)
      D00111=>COM(00040);D00112=>COM(00041);D00113=>COM(00042)
      D00114=>COM(00043);D00115=>COM(00044);D00165=>COM(00045)
      D00164=>COM(00046);D00163=>COM(00047);D00162=>COM(00048)
      D00161=>COM(00049);C00072=>COM(00050);D00300=>COM(00051)
      D00299=>COM(00052);D00298=>COM(00053);D00297=>COM(00054)
      D00296=>COM(00055)
      END SUBROUTINE ASSOCNMPRD4
      END MODULE NMPRD4P
      SUBROUTINE PK(ICALL,IDEF,THETA,IREV,EVTREC,NVNT,INDXS,IRGG,GG,NETAS)    
      USE NMPRD4P
      USE SIZES,     ONLY: DPSIZE,ISIZE
      USE NMPRD_REAL,ONLY: ETA,EPS                                            
      USE NMPRD_INT, ONLY: MSEC=>ISECDER,MFIRST=>IFRSTDER,COMACT,COMSAV
      USE NMPRD_INT, ONLY: IQUIT
      USE PRCM_INT,  ONLY: MC0000=>PRMC,ME0000=>PRME                          
      USE PRCM_INT,  ONLY: MG0000=>PRMG,MT0000=>PRMT                          
      USE PROCM_INT, ONLY: NEWIND=>PNEWIF                                     
      USE NMBAYES_REAL, ONLY: LDF                                             
      IMPLICIT REAL(KIND=DPSIZE) (A-Z)                                        
      REAL(KIND=DPSIZE) :: EVTREC                                             
      SAVE
      INTEGER(KIND=ISIZE) :: ICALL,IDEF,IREV,NVNT,INDXS,IRGG,NETAS            
      DIMENSION :: IDEF(7,*),THETA(*),EVTREC(IREV,*),INDXS(*),GG(IRGG,71,*)
      IF (ICALL <= 1) THEN                                                    
      CALL ASSOCNMPRD4
      MC0000(1)=30
      ME0000(1)=70
      MG0000(1)=080
      MT0000(1)=70
      IDEF(1,001)= -9
      IDEF(1,002)= -1
      IDEF(1,003)=  0
      IDEF(1,004)=  0
      IDEF(2,003)=  0
      IDEF(2,004)=  0
      IDEF(3,002)=  7
      IDEF(4,001)=  6
      CALL GETETA(ETA)                                                        
      IF (IQUIT == 1) RETURN                                                  
      RETURN                                                                  
      ENDIF                                                                   
      IF (NEWIND /= 2) THEN
       IF (ICALL == 4) THEN
        CALL SIMETA(ETA)
       ELSE
        CALL GETETA(ETA)
       ENDIF
       IF (IQUIT == 1) RETURN
      ENDIF
      PROT=EVTREC(NVNT,03)
      AGE=EVTREC(NVNT,12)
      DOSE=EVTREC(NVNT,13)
      B00001=AGE-54.00D0 
      B00002=1.D0+THETA(07)*B00001 
      CLAGE=B00002 
      CLCOV=CLAGE 
      STRT=PROT*1000.D0+DOSE 
      TVCL=DEXP(THETA(01)) 
      TVCL=CLCOV*TVCL 
      MU_1=DLOG(TVCL) 
      B00004=MU_1+ETA(01) 
      B00005=DEXP(B00004) 
      CL=B00005 
!                      A00073 = DERIVATIVE OF CL W.R.T. ETA(01)
      A00073=B00005 
      TVV2=DEXP(THETA(02)) 
      MU_2=DLOG(TVV2) 
      B00006=MU_2+ETA(02) 
      B00007=DEXP(B00006) 
      V2=B00007 
!                      A00078 = DERIVATIVE OF V2 W.R.T. ETA(02)
      A00078=B00007 
      TVKA=THETA(03) 
      MU_3=DLOG(TVKA) 
      B00008=MU_3+ETA(03) 
      B00009=DEXP(B00008) 
      KA=B00009 
!                      A00083 = DERIVATIVE OF KA W.R.T. ETA(03)
      A00083=B00009 
      TVF1=THETA(04) 
      F1=TVF1 
      TVQ=DEXP(THETA(05)) 
      MU_4=DLOG(TVQ) 
      B00010=MU_4+ETA(04) 
      B00011=DEXP(B00010) 
      Q=B00011 
!                      A00088 = DERIVATIVE OF Q W.R.T. ETA(04)
      A00088=B00011 
      TVV3=DEXP(THETA(06)) 
      MU_5=DLOG(TVV3) 
      B00012=MU_5+ETA(05) 
      B00013=DEXP(B00012) 
      V3=B00013 
!                      A00093 = DERIVATIVE OF V3 W.R.T. ETA(05)
      A00093=B00013 
      S2=V2*624.709D0/1.D6 
      B00014=624.709D0/1.D6 
!                      A00096 = DERIVATIVE OF S2 W.R.T. ETA(02)
      A00096=B00014*A00078 
      GG(01,1,1)=CL    
      GG(01,02,1)=A00073
      GG(02,1,1)=V2    
      GG(02,03,1)=A00078
      GG(03,1,1)=Q     
      GG(03,05,1)=A00088
      GG(04,1,1)=V3    
      GG(04,06,1)=A00093
      GG(05,1,1)=KA    
      GG(05,04,1)=A00083
      GG(06,1,1)=F1    
      GG(07,1,1)=S2    
      GG(07,03,1)=A00096
      IF (MSEC == 1) THEN
!                      A00075 = DERIVATIVE OF A00073 W.R.T. ETA(01)
      A00075=B00005 
!                      A00080 = DERIVATIVE OF A00078 W.R.T. ETA(02)
      A00080=B00007 
!                      A00085 = DERIVATIVE OF A00083 W.R.T. ETA(03)
      A00085=B00009 
!                      A00090 = DERIVATIVE OF A00088 W.R.T. ETA(04)
      A00090=B00011 
!                      A00095 = DERIVATIVE OF A00093 W.R.T. ETA(05)
      A00095=B00013 
!                      A00097 = DERIVATIVE OF A00096 W.R.T. ETA(02)
      A00097=B00014*A00080 
      GG(01,02,02)=A00075
      GG(02,03,03)=A00080
      GG(03,05,05)=A00090
      GG(04,06,06)=A00095
      GG(05,04,04)=A00085
      GG(07,03,03)=A00097
      ENDIF
      RETURN
      END
      SUBROUTINE ERROR (ICALL,IDEF,THETA,IREV,EVTREC,NVNT,INDXS,F,G,HH)       
      USE NMPRD4P
      USE SIZES,     ONLY: DPSIZE,ISIZE
      USE NMPRD_REAL,ONLY: ETA,EPS                                            
      USE NMPRD_INT, ONLY: MSEC=>ISECDER,MFIRST=>IFRSTDER,IQUIT
      USE NMPRD_INT, ONLY: NEWL2
      USE PRCM_INT,  ONLY: MC0000=>PRMC,ME0000=>PRME                          
      USE PRCM_INT,  ONLY: MG0000=>PRMG,MT0000=>PRMT                          
      USE PROCM_INT, ONLY: NEWIND=>PNEWIF                                     
      IMPLICIT REAL(KIND=DPSIZE) (A-Z)                                        
      REAL(KIND=DPSIZE) :: EVTREC                                             
      SAVE
      INTEGER(KIND=ISIZE) :: ICALL,IDEF,IREV,NVNT,INDXS                       
      DIMENSION :: IDEF(*),THETA(*),EVTREC(IREV,*),INDXS(*),G(70,*)
      DIMENSION :: HH(70,*)
      IF (ICALL <= 1) THEN                                                    
      CALL ASSOCNMPRD4
      MC0000(2)=30
      ME0000(2)=70
      MG0000(2)=080
      MT0000(2)=70
      IDEF(2)=-1
      IDEF(3)=00
      RETURN
      ENDIF
      IF (ICALL == 4) THEN
       IF (NEWL2 == 1) THEN
        CALL SIMEPS(EPS)
        IF (IQUIT == 1) RETURN
       END IF
      ENDIF
      D00001=G(01,1)
      D00002=G(02,1)
      D00003=G(03,1)
      D00004=G(04,1)
      D00005=G(05,1)
      DV=EVTREC(NVNT,06)
      IPRED=F 
      IRES=DV-IPRED 
!                      D00111 = DERIVATIVE OF IRES W.R.T. ETA(01)
      D00111=-D00001 
!                      D00112 = DERIVATIVE OF IRES W.R.T. ETA(02)
      D00112=-D00002 
!                      D00113 = DERIVATIVE OF IRES W.R.T. ETA(03)
      D00113=-D00003 
!                      D00114 = DERIVATIVE OF IRES W.R.T. ETA(04)
      D00114=-D00004 
!                      D00115 = DERIVATIVE OF IRES W.R.T. ETA(05)
      D00115=-D00005 
      DEL=0.D0 
      IF(F == 0.D0)THEN 
      DEL=10.D0 
      ENDIF 
      W=F 
      B00004=W+DEL 
      IWRES=IRES/B00004 
      B00005=1.D0/B00004 
!                      D00156 = DERIVATIVE OF IWRES W.R.T. ETA(05)
      D00156=B00005*D00115 
!                      D00157 = DERIVATIVE OF IWRES W.R.T. ETA(04)
      D00157=B00005*D00114 
!                      D00158 = DERIVATIVE OF IWRES W.R.T. ETA(03)
      D00158=B00005*D00113 
!                      D00159 = DERIVATIVE OF IWRES W.R.T. ETA(02)
      D00159=B00005*D00112 
!                      D00160 = DERIVATIVE OF IWRES W.R.T. ETA(01)
      D00160=B00005*D00111 
      B00006=-IRES/B00004/B00004 
!                      D00161 = DERIVATIVE OF IWRES W.R.T. ETA(05)
      D00161=B00006*D00005+D00156 
!                      D00162 = DERIVATIVE OF IWRES W.R.T. ETA(04)
      D00162=B00006*D00004+D00157 
!                      D00163 = DERIVATIVE OF IWRES W.R.T. ETA(03)
      D00163=B00006*D00003+D00158 
!                      D00164 = DERIVATIVE OF IWRES W.R.T. ETA(02)
      D00164=B00006*D00002+D00159 
!                      D00165 = DERIVATIVE OF IWRES W.R.T. ETA(01)
      D00165=B00006*D00001+D00160 
      B00012=1.D0+EPS(01) 
      Y=F*B00012 
!                      C00072 = DERIVATIVE OF Y W.R.T. EPS(01)
      C00072=F 
!                      D00296 = DERIVATIVE OF C00072 W.R.T. ETA(05)
      D00296=D00005 
!                      D00297 = DERIVATIVE OF C00072 W.R.T. ETA(04)
      D00297=D00004 
!                      D00298 = DERIVATIVE OF C00072 W.R.T. ETA(03)
      D00298=D00003 
!                      D00299 = DERIVATIVE OF C00072 W.R.T. ETA(02)
      D00299=D00002 
!                      D00300 = DERIVATIVE OF C00072 W.R.T. ETA(01)
      D00300=D00001 
      HH(01,1)=C00072 
      HH(01,02)=D00300
      HH(01,03)=D00299
      HH(01,04)=D00298
      HH(01,05)=D00297
      HH(01,06)=D00296
      F=Y
      RETURN
      END
      SUBROUTINE MUMODEL2(THETA,MU_,ICALL,IDEF,NEWIND,&
      EVTREC,DATREC,IREV,NVNT,INDXS,F,G,H,IRGG,GG,NETAS)
      USE NMPRD4P
      USE SIZES,     ONLY: DPSIZE,ISIZE
      USE NMPRD_REAL,ONLY: ETA,EPS
      USE NMPRD_INT, ONLY: MSEC=>ISECDER,MFIRST=>IFRSTDER,COMACT,COMSAV
      USE NMPRD_INT, ONLY: IQUIT
      USE PRCM_INT,  ONLY: MC0000=>PRMC,ME0000=>PRME
      USE PRCM_INT,  ONLY: MG0000=>PRMG,MT0000=>PRMT
      USE NMBAYES_REAL, ONLY: LDF
      IMPLICIT REAL(KIND=DPSIZE) (A-Z)
      REAL(KIND=DPSIZE)   :: MU_(*)
      INTEGER NEWIND
      REAL(KIND=DPSIZE) :: EVTREC
      SAVE
      INTEGER(KIND=ISIZE) :: ICALL,IDEF,IREV,NVNT,INDXS,IRGG,NETAS
      DIMENSION :: IDEF(7,*),THETA(*),EVTREC(IREV,*),INDXS(*),GG(IRGG,71,*)
      IF (ICALL <= 1) THEN
      CALL ASSOCNMPRD4
      MC0000(1)=30
      ME0000(1)=70
      MG0000(1)=080
      MT0000(1)=70
      IDEF(1,001)= -9
      IDEF(1,002)= -1
      IDEF(1,003)=  0
      IDEF(1,004)=  0
      IDEF(2,003)=  0
      IDEF(2,004)=  0
      IDEF(3,002)=  7
      IDEF(4,001)=  6
      CALL GETETA(ETA)
      IF (IQUIT == 1) RETURN
      RETURN
      ENDIF
      IF (NEWIND /= 2) THEN
       IF (ICALL == 4) THEN
        CALL SIMETA(ETA)
       ELSE
        CALL GETETA(ETA)
       ENDIF
       IF (IQUIT == 1) RETURN
      ENDIF
      PROT=EVTREC(NVNT,03)
      AGE=EVTREC(NVNT,12)
      DOSE=EVTREC(NVNT,13)
      B00001=AGE-54.00D0
      B00002=1.D0+THETA(07)*B00001
      CLAGE=B00002
      CLCOV=CLAGE
      STRT=PROT*1000.D0+DOSE
      TVCL=DEXP(THETA(01))
      TVCL=CLCOV*TVCL
      MU_1=DLOG(TVCL)
      MU_(001)=MU_1
      B00004=MU_1+ETA(01)
      B00005=DEXP(B00004)
      CL=B00005
!                      A00073 = DERIVATIVE OF CL W.R.T. ETA(01)
      A00073=B00005
      TVV2=DEXP(THETA(02))
      MU_2=DLOG(TVV2)
      MU_(002)=MU_2
      B00006=MU_2+ETA(02)
      B00007=DEXP(B00006)
      V2=B00007
!                      A00078 = DERIVATIVE OF V2 W.R.T. ETA(02)
      A00078=B00007
      TVKA=THETA(03)
      MU_3=DLOG(TVKA)
      MU_(003)=MU_3
      B00008=MU_3+ETA(03)
      B00009=DEXP(B00008)
      KA=B00009
!                      A00083 = DERIVATIVE OF KA W.R.T. ETA(03)
      A00083=B00009
      TVF1=THETA(04)
      F1=TVF1
      TVQ=DEXP(THETA(05))
      MU_4=DLOG(TVQ)
      MU_(004)=MU_4
      B00010=MU_4+ETA(04)
      B00011=DEXP(B00010)
      Q=B00011
!                      A00088 = DERIVATIVE OF Q W.R.T. ETA(04)
      A00088=B00011
      TVV3=DEXP(THETA(06))
      MU_5=DLOG(TVV3)
      MU_(005)=MU_5
       RETURN
      B00012=MU_5+ETA(05)
      B00013=DEXP(B00012)
      V3=B00013
!                      A00093 = DERIVATIVE OF V3 W.R.T. ETA(05)
      A00093=B00013
      S2=V2*624.709D0/1.D6
      B00014=624.709D0/1.D6
!                      A00096 = DERIVATIVE OF S2 W.R.T. ETA(02)
      A00096=B00014*A00078
      GG(01,1,1)=CL
      GG(01,02,1)=A00073
      GG(02,1,1)=V2
      GG(02,03,1)=A00078
      GG(03,1,1)=Q
      GG(03,05,1)=A00088
      GG(04,1,1)=V3
      GG(04,06,1)=A00093
      GG(05,1,1)=KA
      GG(05,04,1)=A00083
      GG(06,1,1)=F1
      GG(07,1,1)=S2
      GG(07,03,1)=A00096
      IF (MSEC == 1) THEN
!                      A00075 = DERIVATIVE OF A00073 W.R.T. ETA(01)
      A00075=B00005
!                      A00080 = DERIVATIVE OF A00078 W.R.T. ETA(02)
      A00080=B00007
!                      A00085 = DERIVATIVE OF A00083 W.R.T. ETA(03)
      A00085=B00009
!                      A00090 = DERIVATIVE OF A00088 W.R.T. ETA(04)
      A00090=B00011
!                      A00095 = DERIVATIVE OF A00093 W.R.T. ETA(05)
      A00095=B00013
!                      A00097 = DERIVATIVE OF A00096 W.R.T. ETA(02)
      A00097=B00014*A00080
      GG(01,02,02)=A00075
      GG(02,03,03)=A00080
      GG(03,05,05)=A00090
      GG(04,06,06)=A00095
      GG(05,04,04)=A00085
      GG(07,03,03)=A00097
      ENDIF
      RETURN
      END
