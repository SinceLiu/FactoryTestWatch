
#ifndef __ASM_ARCH_MB86A29_H_
#define __ASM_ARCH_MB86A29_H_


#define Tuner_Adr_DEFAULT             0x20
#define IF_freq_kHz_DEFAULT           MB86A2x_IF_FREQ_440_kHz
#define MB86A2x_CH_UNIT_DEFAULT       MB86A2x_CH_UNIT_JP
//#define MB86A2x_CH_UNIT_DEFAULT          MB86A2x_CH_UNIT_BZ        /* Channel unit = Brazil */
#define MB86A2x_CH_DEFAULT            13
#define RF_freq_kHz_DEFAULT           473143

#define MB86A2x_Adr_DEFAULT           MB86A2x_DEV_ADR_20
#define IQ_pol_DEFAULT                1    /* value after reset=0 */
#define POWER_DOWN_DEFAULT            0
#define RF_CONT_EN_DEFAULT            1

#define INPUT_CLK_FREQ_kHz_DEFAULT    33338
#define REFSEL_DEFAULT                MB86A2x_REFSEL_16MHz
#define PLLPASS_DEFAULT               1

#define RECEP_SEG_DEFAULT             MB86A2x_RECEP_SEG_B_64QAM

#define MAIF_DEFAULT                  0    /* value after reset=0 */
#define MARF_DEFAULT                  0    /* value after reset=0 */
#define IFAGC_POL_DEFAULT             1    /* value after reset=1 */
#define RFAGC_POL_DEFAULT             1    /* value after reset=1 */
#define RFAGC_ON_DEFAULT              0

#define VIFREF_DEFAULT                50    /* value after reset=50(d) */
#define AGAIN_DEFAULT                 120    /* value after reset=120(d) */
#define DTS_DEFAULT                   3        /* value after reset=255(d) */
#define IFA_DEFAULT                   53    /* value after reset=53(d) */
#define IFB_DEFAULT                   3800    /* value after reset=3800(d) */
#define IFAGCO_DEFAULT                0        /* value after reset=0(d) */
#define MAXIFAGC_DEFAULT              255    /* value after reset=0(d) */
#define IFSAMPLE_DEFAULT              100    /* value after reset=100(d) */
#define OUTSAMPLE_DEFAULT             100    /* value after reset=100(d) */
#define VMAIF_DEFAULT                 0        /* value after reset=0(d) */

#define DRF_DEFAULT                   20000    /* value after reset=20000(d) */
#define BGAIN_DEFAULT                 2        /* value after reset=20000(d) */
#define RFA_DEFAULT                   34    /* value after reset=34(d) */
#define RFB_DEFAULT                   3800    /* value after reset=3800(d) */
#define RFAGCO_DEFAULT                0        /* value after reset=0(d) */
#define MAXRFAGC_DEFAULT              255    /* value after reset=255(d) */
#define VMARF_DEFAULT                 0        /* value after reset=0(d) */

#define S1CNT_DEFAULT                 60    /* value after reset=0(d) */

//#define TS_OUT_SEL_DEFAULT            MB86A2x_TS_OUT_PARALLEL
#define TS_OUT_SEL_DEFAULT            MB86A2x_TS_OUT_SERIAL
#define TS_LAYER_DEFAULT              MB86A2x_TS_LAYER_ALL
#define TS_CLK_DEFALT                 MB86A2x_TS_CLK_NEG
#define TS_ERR_MASK_DEFAULT           MB86A2x_TS_ERR_MASK_OFF
#define TS_PARITY_DEFAULT             MB86A2x_TS_RS_PARITY_ON
#define TS_SYNC_DEFAULT               MB86A2x_TS_SYNC_ON

#define VBER_A_NUM_DEFAULT            0x000fff    /* value after reset=08_00_00(h) */
#define VBER_B_NUM_DEFAULT            0x00ffff    /* value after reset=20_00_00(h) */
#define VBER_C_NUM_DEFAULT            0x00ffff    /* value after reset=20_00_00(h) */
#define SBER_A_NUM_DEFAULT            0x0003ff    /* value after reset=1F_FF(h) */
#define SBER_B_NUM_DEFAULT            0x003fff    /* value after reset=1F_FF(h) */
#define SBER_C_NUM_DEFAULT            0x003fff    /* value after reset=1F_FF(h) */
#define PBER_A_NUM_DEFAULT            0x0003ff    /* value after reset=00_FF(h) */
#define PBER_B_NUM_DEFAULT            0x003fff    /* value after reset=00_FF(h) */
#define PBER_C_NUM_DEFAULT            0x003fff    /* value after reset=00_FF(h) */
#define CN_SYMBOL_DEFAULT             0x04    /* value after reset=4(h) */
#define MER_SYMBOL_DEFAULT            0x04    /* value after reset=7(h) */

#define START_CH_DEFAULT              14
#define STOP_CH_DEFAULT               63
#define WAIT_CH_SCH_STAT_DEFAULT      10
#define WAIT_CH_SCH_SEQ_DEFAULT       20
#define SCH_LOOP_MAX_DEFAULT          50


/****************************************************************************
*       Imports and definitions for WIN32                             
****************************************************************************/
typedef unsigned char  UINT8 ;
typedef unsigned short UINT16 ;
typedef unsigned int   UINT32 ;
typedef char           SINT8 ;
typedef short          SINT16 ;
typedef int            SINT32 ;
//typedef double         REAL32 ;
typedef int        REAL32 ;




#define D_DTVD_TUNER_REQ_PWRON  _IOW('v', 0x02, unsigned long)
#define D_DTVD_TUNER_REQ_INIT _IOW('v', 0x01, unsigned long)
#define D_DTVD_TUNER_REQ_TUNE_DTV _IOW('v', 0x03, unsigned long)
#define D_DTVD_TUNER_REQ_STOP _IOW('v', 0x04, unsigned long)
#define D_DTVD_TUNER_REQ_END _IOW('v', 0x05, unsigned long)
#define D_DTVD_TUNER_REQ_PWROFF _IOW('v', 0x06, unsigned long)
#define D_DTVD_TUNER_REQ_GET_CN _IOW('v', 0x07, unsigned long)
#define D_DTVD_TUNER_REQ_GET_INFO _IOW('v', 0x08, unsigned long)
#define D_DTVD_TUNER_REQ_SET_STYLE _IOW('v', 0x09, unsigned long)
#define D_DTVD_TUNER_REQ_SET_ECO _IOW('v', 0x0a, unsigned long)
#define D_DTVD_TUNER_REQ_INIT_OMT _IOW('v', 0x0b, unsigned long)
#define D_DTVD_TUNER_REQ_STOP_OMT _IOW('v', 0x0c, unsigned long)
#define D_DTVD_TUNER_REQ_TUNE_DTV_OMT _IOW('v', 0x0d, unsigned long)
#define D_DTVD_TUNER_REQ_BER_START_OMT _IOW('v', 0x0e, unsigned long)
#define D_DTVD_TUNER_REQ_BER_STOP_OMT _IOW('v', 0x0f, unsigned long)
#define D_DTVD_TUNER_REQ_BER_GET_OMT _IOW('v', 0x10, unsigned long)
#define D_DTVD_TUNER_REQ_CNR_START_OMT _IOW('v', 0x11, unsigned long)
#define D_DTVD_TUNER_REQ_CNR_STOP_OMT _IOW('v', 0x12, unsigned long)
#define D_DTVD_TUNER_REQ_CNR_GET_OMT _IOW('v', 0x13, unsigned long)
#define D_DTVD_TUNER_REQ_AGC_START_OMT _IOW('v', 0x14, unsigned long)
#define D_DTVD_TUNER_REQ_AGC_STOP_OMT _IOW('v', 0x15, unsigned long)
#define D_DTVD_TUNER_REQ_AGC_GET_OMT _IOW('v', 0x16, unsigned long)
#define D_DTVD_TUNER_REQ_ANT_SET_OMT _IOW('v', 0x17, unsigned long)



int dtv_cit_test (int *args);


/****************************************************************************
*    MB86A21 Error message*/
typedef enum {
    MB86A2x_OK         =  0,    /* function execution was successful */
    MB86A2x_ERR_INIT   =  1,    /* I2C bus communication problem */
    MB86A2x_ERR_RFTUNE =  2,    /* I2C bus communication problem in RF tuning */
    MB86A2x_ERR_OTHERS = 10        /* other problem */
} MB86A2x_ERR_MSG ;


/****************************************************************************
*    MB86A21 Device Address
****************************************************************************/
typedef enum {
    MB86A2x_DEV_ADR_20 = 0x20,    //ADR1=0, ADR0=0
    MB86A2x_DEV_ADR_22 = 0x22,    //ADR1=0, ADR0=1
    MB86A2x_DEV_ADR_24 = 0x24,    //ADR1=1, ADR0=0
    MB86A2x_DEV_ADR_26 = 0x26    //ADR1=1, ADR0=1
} MB86A2x_DEV_ADR ;


/****************************************************************************
*    MB86A21 IF Frequency
****************************************************************************/
typedef enum {
    MB86A2x_IF_FREQ_000_kHz =     0,    /* IF frequency =  0.0MHz (IQ) */
    MB86A2x_IF_FREQ_033_kHz =  3300,    /* IF frequency =  3.3MHz */
    MB86A2x_IF_FREQ_040_kHz =  4000,    /* IF frequency =  4.0MHz */
    MB86A2x_IF_FREQ_440_kHz = 44000,    /* IF frequency = 44.0MHz */
    MB86A2x_IF_FREQ_570_kHz = 57000        /* IF frequency = 57.0MHz */
} MB86A2x_IF_IFREQ ;


/****************************************************************************
*    MB86A21 Reception Segment Setting
****************************************************************************/
typedef enum {
    MB86A2x_RECEP_SEG_A_64QAM = 0,    /* Layer-A 64QAM */
    MB86A2x_RECEP_SEG_A_OTHER = 1,    /* Layer-A Other Mod */
    MB86A2x_RECEP_SEG_B_64QAM = 2,    /* Layer-B 64QAM */
    MB86A2x_RECEP_SEG_B_OTHER = 3,    /* Layer-B Other Mod */
    MB86A2x_RECEP_SEG_C_64QAM = 4,    /* Layer-C 64QAM */
    MB86A2x_RECEP_SEG_C_OTHER = 5,    /* Layer-C Other Mod */
} MB86A2x_RECEP_SEG ;


/****************************************************************************
*    MB86A21 Reception Layer Setting
****************************************************************************/
typedef enum {
    MB86A2x_RECEP_LAYER_A = 0,    /* Layer-A */
    MB86A2x_RECEP_LAYER_B = 1,    /* Layer-B */
    MB86A2x_RECEP_LAYER_C = 2,    /* Layer-C */
    MB86A2x_RECEP_HIGHEST = 3    /* Highest Layer */
} MB86A2x_RECEP_LAYER ;


/****************************************************************************
*    MB86A21 Channel Unit
****************************************************************************/
typedef enum {
    MB86A2x_CH_UNIT_JP = 0,        /* Channel unit = Japan */
    MB86A2x_CH_UNIT_BZ = 1,        /* Channel unit = Brazil */
    MB86A2x_CH_UNIT_kHz = 2        /* Channel unit = kHz */
} MB86A2x_CH_UNIT ;


/****************************************************************************
*    MB86A21 PLL Reference Clock Frequency
****************************************************************************/
typedef enum {
    MB86A2x_REFSEL_04MHz = 0,    /* PLL reference clock =  4MHz */
    MB86A2x_REFSEL_16MHz = 1,    /* PLL reference clock = 16MHz */
    MB86A2x_REFSEL_26MHz = 2,    /* PLL reference clock = 26MHz */
    MB86A2x_REFSEL_32MHz = 3    /* PLL reference clock = 32MHz */

} MB86A2x_REFSEL ;


/****************************************************************************
*    MB86A21 TS Output Selection
****************************************************************************/
typedef enum {
    MB86A2x_TS_OUT_PARALLEL = 0,    /* TS Output = Parallel */
    MB86A2x_TS_OUT_SERIAL   = 1        /* TS Output = Serial */
} MB86A2x_TS_OUT_SEL ;


/****************************************************************************
*    MB86A21 Error Packet Mask Function Selection
****************************************************************************/
typedef enum {
    MB86A2x_TS_ERR_MASK_OFF = 0,    /* Error packet = as it is */
    MB86A2x_TS_ERR_MASK_ON  = 1        /* Error packet = changed to null packet */
} MB86A2x_ERR_PAC_MASK ;


/****************************************************************************
*    MB86A21 Parallel TS Output Layer Selection
****************************************************************************/
typedef enum {
    MB86A2x_TS_LAYER_NULL = 0,    /* TS Output layer = null */
    MB86A2x_TS_LAYER_A    = 1,    /* TS Output layer = layer-A */
    MB86A2x_TS_LAYER_B    = 2,    /* TS Output layer = layer-B */
    MB86A2x_TS_LAYER_C    = 3,    /* TS Output layer = layer-C */
    MB86A2x_TS_LAYER_AB   = 4,    /* TS Output layer = layer-A,B */
    MB86A2x_TS_LAYER_AC   = 5,    /* TS Output layer = layer-A,C */
    MB86A2x_TS_LAYER_BC   = 6,    /* TS Output layer = layer-B,C */
    MB86A2x_TS_LAYER_ALL  = 7    /* TS Output layer = layer-A,B,C and null */
} MB86A2x_TS_LAYER ;


/****************************************************************************
*    MB86A21 TS Output Clock Polarity Setting
****************************************************************************/
typedef enum {
    MB86A2x_TS_CLK_NEG = 0,    /* TS Output clock = negative edge */
    MB86A2x_TS_CLK_POS = 1    /* TS Output clock = positive edge */
} MB86A2x_TS_CLK ;


/****************************************************************************
*    MB86A21 TS Output RS Parity Byte Setting
****************************************************************************/
typedef enum {
    MB86A2x_TS_RS_PARITY_OFF = 0,    /* TS Parity Output = Off */
    MB86A2x_TS_RS_PARITY_ON  = 1    /* TS Parity Output = On */
} MB86A2x_TS_RS_PARITY ;


/****************************************************************************
*    MB86A21 TS Output Sync Byte Setting
****************************************************************************/
typedef enum {
    MB86A2x_TS_SYNC_OFF = 0,    /* TS Sync Byte Output = Off */
    MB86A2x_TS_SYNC_ON  = 1        /* TS Sync Byte Output = On */
} MB86A2x_TS_SYNC ;





//  Tuner Configuration
typedef struct
{
    UINT8           Tuner_Adr ;
    UINT8           Tuner_Data[5] ;
    UINT32          IF_freq_kHz ;
    MB86A2x_CH_UNIT CH_UNIT ;
    UINT32          RF_freq_kHz ;
    UINT32          CH ;
} TUNER_PARAM ;


//  Tuner Error Message

typedef enum {      
    TUNER_NG       = 0,
    TUNER_OK       = 1,
} TUNER_MSG ;





/****************************************************************************
*    MB86A21 VERSION Parameter
****************************************************************************/
typedef struct
{
    UINT8  LSI_VERSION ;    /* Demodulator version */
    UINT16 DRV_VERSION ;    /* Driver version */
} MB86A2x_VERSION_INFO;


/****************************************************************************
*    MB86A21 NIM Parameter
****************************************************************************/
typedef struct 
{
    TUNER_PARAM     tuner_param ;
    MB86A2x_DEV_ADR MB86A2x_Adr ;    /* tuner device address */
    UINT8           IQ_pol ;        /* IQ polarity */
    UINT8           POWER_DOWN ;    /* Power Down */
    UINT8           RF_CONT_EN ;    /* RF control enable(Win software only) */
} MB86A2x_NIM_PARAM ;


/****************************************************************************
*    MB86A21 PLL Parameter
****************************************************************************/
typedef struct 
{
    UINT32         INPUT_CLK_FREQ_kHz ;        /* input clock frequency */
    UINT32         PLLPASS ;                /* PLL bypass enable */
    MB86A2x_REFSEL REFSEL ;                    /* reference frequency selection */
    UINT32         MASTER_CLK_FREQ_kHz ;    /* mater clock frequency */
} MB86A2x_PLL_PARAM ;


/****************************************************************************
*    MB86A21 Segment Parameter
****************************************************************************/
typedef struct 
{
    MB86A2x_RECEP_SEG RECEP_SEG ;    /* recepion segment */
} MB86A2x_SEG_PARAM ;


/****************************************************************************
*    MB86A21 Recovery Parameter
****************************************************************************/
typedef struct 
{
    UINT32 CRR_OFS ;    /* carrier recovery offset value */
    UINT32 STR_OFS ;    /* symbol timing recovery offset value */
} MB86A2x_RECOV_PARAM ;


/****************************************************************************
*    MB86A21 AGC Parameter
****************************************************************************/
typedef struct 
{
    UINT8 MAIF ;        /* IFAGC Manual setting */
    UINT8 MARF ;        /* RFAGC Manual setting */
    UINT8 IFAGC_POL ;    /* IFAGC polarity */
    UINT8 RFAGC_POL ;    /* RFAGC polarity */
    UINT8 RFAGC_ON ;    /* RFAGC Switch */
} MB86A2x_AGC_PARAM ;


/****************************************************************************
*    MB86A21 IFAGC Parameter
****************************************************************************/
typedef struct 
{
    UINT32 VIFREF ;        /* IFAGC reference value */
    UINT8  AGAIN ;        /* IFAGC gain setting */
    UINT32 DTS ;        /* filter coefficient of amplitude detector */
    UINT32 IFA ;        /* filter coefficient of IFAGC (alpha) */
    UINT32 IFB ;        /* filter coefficient of IFAGC (beta) */
    UINT8  IFAGCO ;        /* IFAGC offset value */
    UINT8  MAXIFAGC ;    /* maximum output value of AGC */
    UINT8  IFSAMPLE ;    /* sampling frequency of amplitude detector */
    UINT8  OUTSAMPLE ;    /* sampling frequency of loop filter */
    UINT8  VMAIF ;      /* IFAGCDAC manual setting value */
} MB86A2x_IFAGC_PARAM ;


/****************************************************************************
*    MB86A21 RFAGC Parameter
****************************************************************************/
typedef struct 
{
    UINT32 DRF ;        /* RFAGC reference value */
    UINT8  BGAIN ;        /* RFAGC gain setting */
    UINT32 RFA ;        /* filter coefficient of IFAGC (alpha) */
    UINT32 RFB ;        /* filter coefficient of IFAGC (beta) */
    UINT8  RFAGCO ;        /* IFAGC offset value */
    UINT8  MAXRFAGC ;    /* maximum output value of AGC */
    UINT8  VMARF ;      /* IFAGCDAC manual setting value */
} MB86A2x_RFAGC_PARAM ;


/****************************************************************************
*    MB86A21 Tuner Dependent Parameter
****************************************************************************/
typedef struct 
{
    UINT32 S1CNT ;    /* timer setting of sequencer state 1 */
} MB86A2x_TU_DEP_PARAM ;


/****************************************************************************
*    MB86A21 TS Output Parameter
****************************************************************************/
typedef struct 
{
    MB86A2x_TS_OUT_SEL   TS_OUT_SEL ;    /* TS output selection (serial or parallel) */
    MB86A2x_ERR_PAC_MASK TS_ERR_MASK ;    /* converting error packet to null packet */
    MB86A2x_TS_LAYER     TS_LAYER ;        /* TS output layer selection */
    MB86A2x_TS_CLK       TS_CLK ;        /* TS clock polarity */
    MB86A2x_TS_RS_PARITY TS_PARITY ;    /* TS parity byte output enable */
    MB86A2x_TS_SYNC      TS_SYNC ;        /* TS sync byte output enable */
} MB86A2x_TSOUT_PARAM ;


/****************************************************************************
*    MB86A21 Monitor Timer Parameter
****************************************************************************/
typedef struct 
{
    UINT32 VBER_A_NUM ;    /* bit number setting of pre-Viterbi BER of layer-A */
    UINT32 VBER_B_NUM ;    /* bit number setting of pre-Viterbi BER of layer-B */
    UINT32 VBER_C_NUM ;    /* bit number setting of pre-Viterbi BER of layer-C */
    UINT32 SBER_A_NUM ;    /* packet number setting of post-Viterbi BER of layer-A */
    UINT32 SBER_B_NUM ;    /* packet number setting of post-Viterbi BER of layer-B */
    UINT32 SBER_C_NUM ;    /* packet number setting of post-Viterbi BER of layer-C */
    UINT32 PBER_A_NUM ;    /* packet number setting of packet error rate of layer-A */
    UINT32 PBER_B_NUM ;    /* packet number setting of packet error rate of layer-B */
    UINT32 PBER_C_NUM ;    /* packet number setting of packet error rate of layer-C */
    UINT32 CN_SYMBOL ;    /* symbol number setting of C/N monitor */
    UINT32 MER_SYMBOL ;    /* symbol number setting of C/N monitor */
} MB86A2x_MON_TIM_PARAM ;


/****************************************************************************
*    MB86A21 Channel Search Parameter
****************************************************************************/
typedef struct 
{
    UINT32 START_CH ;            /* channel serch start channel */
    UINT32 STOP_CH ;            /* channel serch stop channel */
    UINT32 WAIT_CH_SCH_STAT ;    /* wait time to check whether the first stage of channel search has finished or not [ms]*/
    UINT32 WAIT_CH_SCH_SEQ ;    /* wait time to check whether the state of the sequencer has reached to 8 or not [ms] */
    UINT32 SCH_LOOP_MAX ;    /* maximum repeat time to check the state of the sequencer */
} MB86A2x_CH_SCH_PARAM ;


/****************************************************************************
*    MB86A21 Configuration
****************************************************************************/
typedef struct 
{
    MB86A2x_NIM_PARAM     nim_param ;
    MB86A2x_PLL_PARAM     pll_param ;
    MB86A2x_SEG_PARAM     seg_param ;
    MB86A2x_RECOV_PARAM   recov_param ;
    MB86A2x_AGC_PARAM     agc_param ;
    MB86A2x_IFAGC_PARAM   ifagc_param ;
    MB86A2x_RFAGC_PARAM   rfagc_param ;
    MB86A2x_TU_DEP_PARAM  tun_dep_param ;
    MB86A2x_TSOUT_PARAM   tsout_param ;
    MB86A2x_MON_TIM_PARAM mon_tim_param ;
    MB86A2x_CH_SCH_PARAM  ch_sch_param ;
} MB86A2x_PARAM ;


/****************************************************************************
*    MB86A21 Monitor value
****************************************************************************/

typedef struct
{
    UINT8 STATE ;    /* state value of the sequencer */
    UINT8 S8_REACH ;
    UINT8 S8_REACH_his ;
} MB86A2x_LOCKNG_INFO ;


typedef struct 
{
    UINT32 IF_INPUT_LEVEL ;    /* IF input level */
    UINT8  IFAGCDAC ;        /* DAC output value of IFAGC */
    UINT8  RFAGCDAC ;        /* DAC output value of IFAGC */
} MB86A2x_AGC_INFO ;


typedef struct
{
    SINT32 STR_freq_error_Hz ;    /* frequency error detected  by symbol timing recovery */
    SINT32 CRR_freq_error_kHz ;    /* frequency error detected  by carrier recovery */
} MB86A2x_FREQ_ERR_INFO ;


typedef struct
{
    UINT8 MODE_current ;    /* mode */
    UINT8 GUARD_current ;    /* guard interval */
    UINT8 SYSTEM_current ;    /* symstem */
    UINT8 EMERG_current ;    /* emergency flag */
    UINT8 CNTDN_current ;    /* tmcc count dwon */
    UINT8 PARTIAL_current ;    /* partila reception */
    UINT8 MOD_A_current ;    /* modulation of layer-A */
    UINT8 MOD_B_current ;    /* modulation of layer-B */
    UINT8 MOD_C_current ;    /* modulation of layer-C */
    UINT8 RATE_A_current ;    /* code rate of layer-A */
    UINT8 RATE_B_current ;    /* code rate of layer-B */
    UINT8 RATE_C_current ;    /* code rate of layer-C */
    UINT8 IL_A_current ;    /* interleave depth of layer-A */
    UINT8 IL_B_current ;    /* interleave depth of layer-B */
    UINT8 IL_C_current ;    /* interleave depth of layer-C */
    UINT8 NUM_A_current ;    /* segment number of layer-A */
    UINT8 NUM_B_current ;    /* segment number of layer-B */
    UINT8 NUM_C_current ;    /* segment number of layer-C */
    UINT32 STR_current ;
} MB86A2x_TMCC_INFO ;


typedef struct
{
    UINT8  VBER_A_update ;    /* Pre-Viterbi BER count update flag of layer-A */
    UINT8  VBER_B_update ;    /* Pre-Viterbi BER count update flag of layer-B */
    UINT8  VBER_C_update ;    /* Pre-Viterbi BER count update flag of layer-C */
    UINT8  SBER_A_update ;    /* Post-Viterbi BER count update flag of layer-A */
    UINT8  SBER_B_update ;    /* Post-Viterbi BER count update flag of layer-B */
    UINT8  SBER_C_update ;    /* Post-Viterbi BER count update flag of layer-C */
    UINT8  PBER_A_update ;    /* packet error count update flag of layer-A */
    UINT8  PBER_B_update ;    /* packet error count update flag of layer-B */
    UINT8  PBER_C_update ;    /* packet error count update flag of layer-C */
    UINT8  CN_update ;        /* C/N monitor couunt update flag */
    UINT8  MER_update ;        /* MER monitor couunt update flag */
    REAL32 VBER_A_current ;    /* Pre-Viterbi BER of layer-A */
    REAL32 VBER_B_current ;    /* Pre-Viterbi BER of layer-B */
    REAL32 VBER_C_current ;    /* Pre-Viterbi BER of layer-C */
    REAL32 SBER_A_current ;    /* Post-Viterbi BER of layer-A */
    REAL32 SBER_B_current ;    /* Post-Viterbi BER of layer-B */
    REAL32 SBER_C_current ;    /* Post-Viterbi BER of layer-C */
    REAL32 PBER_A_current ;    /* Packet error rate of layer-A */
    REAL32 PBER_B_current ;    /* Packet error rate of layer-B */
    REAL32 PBER_C_current ;    /* Packet error rate of layer-C */
    REAL32 CN_current ;        /* estimated C/N from C/N monitor */
    REAL32 MER_A_current ;    /* estimated C/N of layer-A from MER monitor */
    REAL32 MER_B_current ;    /* estimated C/N of layer-B from MER monitor */
    REAL32 MER_C_current ;    /* estimated C/N of layer-C from MER monitor */
} MB86A2x_SIG_QUAL_INFO ;


typedef struct
{
    UINT8  CH_SCH_status ;        /* channel search status (first stage finished or not) */
    UINT8  CH_SCH_result ;        /* channel search result of fitst stage */
    UINT8  Detected_ch_num ;    /* the number of detected channel */
    UINT8  CH_num[70] ;            /* channel number for detected channel */
    UINT8  MODE[70] ;            /* channel search result (mode) */
    UINT8  GUARD[70] ;            /* channel search result (guard) */
    UINT8  MOD_A[70] ;            /* channel search result (layer-A modulation) */
    UINT8  MOD_B[70] ;            /* channel search result (layer-B modulation) */
    UINT8  MOD_C[70] ;            /* channel search result (layer-C modulation) */
    UINT8  RATE_A[70] ;            /* channel search result (layer-A code rate) */
    UINT8  RATE_B[70] ;            /* channel search result (layer-B code rate) */
    UINT8  RATE_C[70] ;            /* channel search result (layer-C code rate) */
    UINT8  IL_A[70] ;            /* channel search result (layer-A interleave depth) */
    UINT8  IL_B[70] ;            /* channel search result (layer-B interleave depth) */
    UINT8  IL_C[70] ;            /* channel search result (layer-C interleave depth) */
    UINT8  NUM_A[70] ;            /* channel search result (layer-A segment number) */
    UINT8  NUM_B[70] ;            /* channel search result (layer-B segment number) */
    UINT8  NUM_C[70] ;            /* channel search result (layer-C segment number) */
    UINT32 STR[70] ;
} MB86A2x_CH_SCH_INFO ;


MB86A2x_ERR_MSG MB86A2x_Set_RF( MB86A2x_NIM_PARAM *nim_param ) ;

MB86A2x_ERR_MSG MB86A2x_Set_VBER_Timer( MB86A2x_NIM_PARAM *nim_param, MB86A2x_MON_TIM_PARAM *mon_tim_param ) ;
MB86A2x_ERR_MSG MB86A2x_Set_SBER_Timer( MB86A2x_NIM_PARAM *nim_param, MB86A2x_MON_TIM_PARAM *mon_tim_param ) ;
MB86A2x_ERR_MSG MB86A2x_Set_PBER_Timer( MB86A2x_NIM_PARAM *nim_param, MB86A2x_MON_TIM_PARAM *mon_tim_param ) ;
MB86A2x_ERR_MSG MB86A2x_Set_CN_Timer( MB86A2x_NIM_PARAM *nim_param, MB86A2x_MON_TIM_PARAM *mon_tim_param ) ;
MB86A2x_ERR_MSG MB86A2x_Set_MER_Timer( MB86A2x_NIM_PARAM *nim_param, MB86A2x_MON_TIM_PARAM *mon_tim_param ) ;

MB86A2x_ERR_MSG MB86A2x_Get_TMCC_Info( MB86A2x_NIM_PARAM *nim_param, MB86A2x_TMCC_INFO *tmcc_info ) ;
MB86A2x_ERR_MSG MB86A2x_Get_TMCC_Sys_Emerg( MB86A2x_NIM_PARAM *nim_param, MB86A2x_TMCC_INFO *tmcc_info ) ;
MB86A2x_ERR_MSG MB86A2x_Get_TMCC_Part_Recep( MB86A2x_NIM_PARAM *nim_param, MB86A2x_TMCC_INFO *tmcc_info ) ;
MB86A2x_ERR_MSG MB86A2x_Get_TMCC_Mod_Lay_A( MB86A2x_NIM_PARAM *nim_param, MB86A2x_TMCC_INFO *tmcc_info ) ;
MB86A2x_ERR_MSG MB86A2x_Get_TMCC_Mod_Lay_B( MB86A2x_NIM_PARAM *nim_param, MB86A2x_TMCC_INFO *tmcc_info ) ;
MB86A2x_ERR_MSG MB86A2x_Get_TMCC_Mod_Lay_C( MB86A2x_NIM_PARAM *nim_param, MB86A2x_TMCC_INFO *tmcc_info ) ;
MB86A2x_ERR_MSG MB86A2x_Get_TMCC_Rate_Lay_A( MB86A2x_NIM_PARAM *nim_param, MB86A2x_TMCC_INFO *tmcc_info ) ;
MB86A2x_ERR_MSG MB86A2x_Get_TMCC_Rate_Lay_B( MB86A2x_NIM_PARAM *nim_param, MB86A2x_TMCC_INFO *tmcc_info ) ;
MB86A2x_ERR_MSG MB86A2x_Get_TMCC_Rate_Lay_C( MB86A2x_NIM_PARAM *nim_param, MB86A2x_TMCC_INFO *tmcc_info ) ;
MB86A2x_ERR_MSG MB86A2x_Get_TMCC_IL_Lay_A( MB86A2x_NIM_PARAM *nim_param, MB86A2x_TMCC_INFO *tmcc_info ) ;
MB86A2x_ERR_MSG MB86A2x_Get_TMCC_IL_Lay_B( MB86A2x_NIM_PARAM *nim_param, MB86A2x_TMCC_INFO *tmcc_info ) ;
MB86A2x_ERR_MSG MB86A2x_Get_TMCC_IL_Lay_C( MB86A2x_NIM_PARAM *nim_param, MB86A2x_TMCC_INFO *tmcc_info ) ;
MB86A2x_ERR_MSG MB86A2x_Get_TMCC_Num_Lay_A( MB86A2x_NIM_PARAM *nim_param, MB86A2x_TMCC_INFO *tmcc_info ) ;
MB86A2x_ERR_MSG MB86A2x_Get_TMCC_Num_Lay_B( MB86A2x_NIM_PARAM *nim_param, MB86A2x_TMCC_INFO *tmcc_info ) ;
MB86A2x_ERR_MSG MB86A2x_Get_TMCC_Num_Lay_C( MB86A2x_NIM_PARAM *nim_param, MB86A2x_TMCC_INFO *tmcc_info ) ;
MB86A2x_ERR_MSG MB86A2x_Reset_VBER_A_Counter( MB86A2x_NIM_PARAM *nim_param ) ;
MB86A2x_ERR_MSG MB86A2x_Reset_VBER_B_Counter( MB86A2x_NIM_PARAM *nim_param ) ;
MB86A2x_ERR_MSG MB86A2x_Reset_VBER_C_Counter( MB86A2x_NIM_PARAM *nim_param ) ;
MB86A2x_ERR_MSG MB86A2x_Reset_VBER_ALL_Counter( MB86A2x_NIM_PARAM *nim_param ) ;
MB86A2x_ERR_MSG MB86A2x_Reset_SBER_ALL_Counter( MB86A2x_NIM_PARAM *nim_param ) ;
MB86A2x_ERR_MSG MB86A2x_Reset_PBER_ALL_Counter( MB86A2x_NIM_PARAM *nim_param ) ;
MB86A2x_ERR_MSG MB86A2x_Reset_CN_Counter( MB86A2x_NIM_PARAM *nim_param, MB86A2x_MON_TIM_PARAM *mon_tim_param ) ;
MB86A2x_ERR_MSG MB86A2x_Reset_MER_Counter( MB86A2x_NIM_PARAM *nim_param ) ;

UINT32 MB86A2x_CNval_to_CN10( UINT32 cn_read_value ) ;
UINT32 MB86A2x_MERval_to_CN10_DQPSK( UINT32 mer_read_vale ) ;
UINT32 MB86A2x_MERval_to_CN10_QPSK( UINT32 mer_read_vale ) ;
UINT32 MB86A2x_MERval_to_CN10_16QAM( UINT32 mer_read_vale ) ;
UINT32 MB86A2x_MERval_to_CN10_64QAM( UINT32 mer_read_vale ) ;

#endif
