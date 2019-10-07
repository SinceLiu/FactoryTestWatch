#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <stdint.h>
#include <sys/ioctl.h>
#include <fcntl.h>
#include <dlfcn.h>
#include <errno.h>
#include <android/log.h>
#include <termios.h>
#include "product_test.h"
#include <signal.h>
#include <string.h>
#include <sys/mman.h>
#include <sys/ipc.h>
#include <sys/stat.h>
#include "bma222.h"
#include "bma222_driver.h"
#include "bma250.h"
#include "bma250_driver.h"
#include "cm3623.h"
#include "akm8975.h"
#ifdef __KERNEL__
#include <linux/ioctl.h>
#include <linux/miscdevice.h>
#include <linux/kernel.h>
#include <linux/types.h>
//#include <linux/common.h>
#include <string.h>
//modify for add heartbeatforPCBA by xiasiping 20140925 start
#include <string.h>
#include <errno.h>
#include <pthread.h>
#include <sys/ioctl.h>
#include <fcntl.h>
#include <sys/types.h>
#include <hardware/hardware.h>
#include "hardware.h"
#if 1
//#include <android/log.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/stat.h>
#include <termios.h>
#endif
//modify for add heartbeatforPCBA by xiasiping 20140925 end
//#include <unistd.h>
//#include <sys/types.h>
//#include <sys/stat.h>
//#include <assert.h>
//#include <pthread.h>
#include <mb86a29.h>
#endif
//20130105 add for test Lkt by lvhongshan start
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <assert.h>
#include <pthread.h>
//20130105 add for test Lkt by lvhongshan end

#define LOG_TAG "JNICIT"
#define SIZEOFBUFFER 256*1024L
#define STK_IOC_MAGIC    'T'
#define STK_CALI_0       _IOR(STK_IOC_MAGIC,0, int)
#define STK_CALI_20      _IOR(STK_IOC_MAGIC,1, int)
#define STK_CALI_30      _IOR(STK_IOC_MAGIC,2, int)
#define STK_SET_CALI_0             _IOR(STK_IOC_MAGIC,3, int)
#define STK_SET_CALI_30            _IOR(STK_IOC_MAGIC,4, int)

#define UPEK_IOCTL_MAGIC 'R'
#define UPEK_IOCTL_POWER_ON   \
        _IO(UPEK_IOCTL_MAGIC, 0)
#define UPEK_IOCTL_POWER_DOWN   \
        _IO(UPEK_IOCTL_MAGIC, 1)
#define UPEK_IOCTL_RESET   \
        _IO(UPEK_IOCTL_MAGIC, 2)
#define UPEK_IOCTL_WRITE   \
        _IOWR(UPEK_IOCTL_MAGIC, 3, struct upek_info)
#define UPEK_IOCTL_READ   \
        _IOWR(UPEK_IOCTL_MAGIC, 4, struct upek_info)
#define UPEK_IOCTL_IMAGE   \
        _IOWR(UPEK_IOCTL_MAGIC, 5, struct image)
//modify for add heartbeatforPCBA by xiasiping 20140925 start
#define HEARTBEAT_MODULE_ID "heartbeat"

#define HEARTBEAT_POWER_ON_FILE "/sys/class/switch/heartbeat/power_state"
#define HEARTBEAT_RESET_FILE "/sys/class/switch/heartbeat/reset_state" 
#if 1 
#define HEARTBEAT_SERIAL_FILE "/dev/ttyHSL3"
#endif


typedef struct hw_module_t {
    /** tag must be initialized to HARDWARE_MODULE_TAG */
    uint32_t tag;

    /**
     * The API version of the implemented module. The module owner is
     * responsible for updating the version when a module interface has
     * changed.
     *
     * The derived modules such as gralloc and audio own and manage this field.
     * The module user must interpret the version field to decide whether or
     * not to inter-operate with the supplied module implementation.
     * For example, SurfaceFlinger is responsible for making sure that
     * it knows how to manage different versions of the gralloc-module API,
     * and AudioFlinger must know how to do the same for audio-module API.
     *
     * The module API version should include a major and a minor component.
     * For example, version 1.0 could be represented as 0x0100. This format
     * implies that versions 0x0100-0x01ff are all API-compatible.
     *
     * In the future, libhardware will expose a hw_get_module_version()
     * (or equivalent) function that will take minimum/maximum supported
     * versions as arguments and would be able to reject modules with
     * versions outside of the supplied range.
     */
    uint16_t module_api_version;
#define version_major module_api_version
    /**
     * version_major/version_minor defines are supplied here for temporary
     * source code compatibility. They will be removed in the next version.
     * ALL clients must convert to the new version format.
     */

    /**
     * The API version of the HAL module interface. This is meant to
     * version the hw_module_t, hw_module_methods_t, and hw_device_t
     * structures and definitions.
     *
     * The HAL interface owns this field. Module users/implementations
     * must NOT rely on this value for version information.
     *
     * Presently, 0 is the only valid value.
     */
    uint16_t hal_api_version;
#define version_minor hal_api_version

    /** Identifier of module */
    const char *id;

    /** Name of this module */
    const char *name;

    /** Author/owner/implementor of the module */
    const char *author;

    /** Modules methods */
    struct hw_module_methods_t* methods;

    /** module's dso */
    void* dso;

    /** padding to 128 bytes, reserved for future use */
    uint32_t reserved[32-7];

} hw_module_t;
typedef struct hw_device_t {
    /** tag must be initialized to HARDWARE_DEVICE_TAG */
    uint32_t tag;

    /**
     * Version of the module-specific device API. This value is used by
     * the derived-module user to manage different device implementations.
     *
     * The module user is responsible for checking the module_api_version
     * and device version fields to ensure that the user is capable of
     * communicating with the specific module implementation.
     *
     * One module can support multiple devices with different versions. This
     * can be useful when a device interface changes in an incompatible way
     * but it is still necessary to support older implementations at the same
     * time. One such example is the Camera 2.0 API.
     *
     * This field is interpreted by the module user and is ignored by the
     * HAL interface itself.
     */
    uint32_t version;

    /** reference to the module this device belongs to */
    struct hw_module_t* module;

    /** padding reserved for future use */
    uint32_t reserved[12];

    /** Close this device */
    int (*close)(struct hw_device_t* device);

} hw_device_t;


struct heartbeat_data {
    int power_state; 
    int reset_state; 
}; 

struct heartbeat_module_t {
    struct hw_module_t common; 
};

struct heartbeat_device_t {
    struct hw_device_t common; 
    struct heartbeat_data heartbeat_data; 
    int (*heartbeat_power_on)(struct heartbeat_device_t* dev); 
    int (*heartbeat_reset)(struct heartbeat_device_t* dev); 
#if 1
    int (*serial_open)(void); 
    int (*serial_close)(void); 
#endif
}; 
//modify for add heartbeatforPCBA by xiasiping 20140925 end

struct upek_info {
    unsigned char addr;  //for address
    unsigned char data;  //for write/read
};

typedef struct
{
    unsigned char addr;  //for address
    unsigned char data;  //for write/read
    unsigned char succ;  //for succ value
    unsigned char tri_state;  //for tri_state, 0-all, 1-low, 2-high
}upek_info_s;

upek_info_s upek_info_table[23] = 
{
    {0x00, 0x9E, 0x9E, 0},
    {0x01, 0x3F, 0x0F, 1},
    {0x02, 0x05, 0x05, 1},
    {0x04, 0x05, 0x05, 1},
    {0x06, 0x05, 0x05, 1},
    {0x07, 0x26, 0x06, 1},
    {0x09, 0x4D, 0x4D, 0},
//    {0x0A, 0x1?, 0x05, 1},    //?
    {0x0B, 0x10, 0x10, 0},
    {0x0C, 0x00, 0x00, 0},
    {0x0D, 0x00, 0x00, 0},
    {0x0E, 0x00, 0x00, 0},
    {0x0F, 0x94, 0x98, 0},
    {0x10, 0x00, 0x00, 0},
    {0x12, 0xFF, 0xFF, 0},
    {0x14, 0x01, 0x01, 0},
    {0x15, 0x00, 0x00, 0},
    {0x17, 0x67, 0x67, 0},
    {0x19, 0x01, 0x01, 0},
    {0x1A, 0x09, 0x09, 0},
    {0x1B, 0x09, 0x09, 0},
    {0x1C, 0x0F, 0x0F, 0},
    {0x1D, 0x00, 0x00, 0},
    {0x1F, 0x94, 0x98, 0}
};

//20130105 add for test Lkt by lvhongshan start
/* IOCTLs for lkt encryption chip*/
#define MSM_LKT_IOC_MAGIC     'L'

#define MSM_LKT_RESET                _IO(MSM_LKT_IOC_MAGIC, 0)

#define MSM_LKT_SET_BIT_RATE              _IOW(MSM_LKT_IOC_MAGIC, 1, unsigned char)

#define MSM_LKT_SLEEP                              _IOW(MSM_LKT_IOC_MAGIC, 2, unsigned char)

#define MSM_LKT_WAKEUP                      _IOW(MSM_LKT_IOC_MAGIC, 3, unsigned char)

#define MSM_LKT_POWER                      _IOW(MSM_LKT_IOC_MAGIC, 4, unsigned char)

/* The type of parity.  */
#define UART_NO_PARITY        0x00
#define UART_ODD_PARITY        0x08
#define UART_EVEN_PARITY    0x18

/* The type of word length.  */
#define UART_5BITS_WORD    0x00
#define UART_6BITS_WORD    0x01
#define UART_7BITS_WORD    0x02
#define UART_8BITS_WORD    0x03

/* The type of the length of stop bit.  */
#define UART_1_STOP_BIT        0x00
#define UART_2_STOP_BITS            0x04

//20130105 add for test Lkt by lvhongshan end

void AddRootPermission()
{
   /*int handle = open("/dev/threadright0", O_RDWR);
    if(handle == -1)
    {
        __android_log_print(4, LOG_TAG, "open(threadright0) failed:fd = %d\n", handle);

    }
        __android_log_print(4, LOG_TAG, "+");
    close(handle);*/
}

//modify for add heartbeatforPCBA by xiasiping 20140925 start
#if 1 
int speed_arr[] = {B115200, B57600, B38400, B19200, B9600, B4800, B2400, B1200, B300, };
int name_arr[] = {115200, 57600, 38400, 19200, 9600, 4800, 2400, 1200, 300, };
int fd_serial; 
//read_serial(); 
/*JNI_set_1speed*/
static void serial_set_speed(int fd, int speed)
{
    struct termios opt; 
    int i; 
    int status; 

    printf("[heartbeat]fd=%d, speed=%d\n", fd, speed); 
    tcgetattr(fd, &opt); 
    for (i=0; i<sizeof(speed_arr)/sizeof(int); i++) {
        if (speed == name_arr[i]) {
            tcflush(fd, TCIOFLUSH); 
            cfsetispeed(&opt, speed_arr[i]); 
            cfsetospeed(&opt, speed_arr[i]);
            status = tcsetattr(fd, TCSANOW, &opt);
            if (status != 0)
                perror("[heartbeat]tcsetattr fd\n");
            return ; 
        }
        tcflush(fd, TCIOFLUSH); 
    }
}
/*SerialJNI_set_1Parity*/
static int serial_set_parity(int fd, int databits, int parity, int stopbits)
{
    struct termios opt; 
    if (tcgetattr(fd, &opt) != 0) {
        perror("[heartbeat]SetupSerial\n");
        return -1; 
    }
/*
    opt.c_cflag &= ~CSIZE; 
    opt.c_lflag &= ~(ICANON|ECHO|ECHOE|ISIG);
    opt.c_oflag &= ~OPOST; 
*/
        opt.c_lflag = 0;
        opt.c_oflag = 0;
        opt.c_iflag = 0;

        opt.c_cflag &= ~CSTOPB;    //stop bit 1
        opt.c_cflag |= CS8;        //data bit 8
        opt.c_cflag &= ~PARENB;    //parity non
        opt.c_cflag &= ~CRTSCTS;        //no flow control, need update kernel
/*
    switch(databits) {
        case 7:
            opt.c_cflag |= CS7; 
            break; 
        case 8:
            opt.c_cflag |= CS8; 
            break; 
        default:
            fprintf(stderr, "Unsupported data size\n");
            return -1; 
    }
    switch(parity) {
        case 'n':
        case 'N':
            opt.c_cflag &= ~PARENB; 
            opt.c_cflag &= ~CRTSCTS; 
            break; 
        case 'o':
        case 'O':
            opt.c_cflag |= (PARODD|PARENB); 
            opt.c_iflag |= INPCK; 
            break; 
        case 'e':
        case 'E':
            opt.c_cflag |= PARENB; 
            opt.c_cflag &= ~PARODD; 
            opt.c_iflag |= INPCK; 
            break; 
        case 's':
            opt.c_cflag &= ~PARENB; 
            opt.c_cflag &= ~CSTOPB; 
            break; 
        default:
            fprintf(stderr, "Unsupported parity\n");
            return -1; 
    }
    switch(stopbits) {
        case 1:
            opt.c_cflag &= ~CSTOPB; 
            break; 
        case 2:
            opt.c_cflag |= ~CSTOPB; 
            break; 
        default:
            fprintf(stderr, "Unsupported stop bits\n");
            return -1; 
    }
    if (parity != 'n')        ios.c_cflag &= ~CSTOPB;    //stop bit 1
        ios.c_cflag |= CS8;        //data bit 8
        ios.c_cflag &= ~PARENB;    //parity non
        ios.c_cflag &= ~CRTSCTS;        //no flow control, need update kernel
        opt.c_iflag |= INPCK; 
        opt.c_cc[VTIME] = 0; 
        opt.c_cc[VMIN] = 0; 
    tcflush(fd, TCIFLUSH);
*/
    if (tcsetattr(fd, TCSANOW, &opt) != 0) {
        perror("[heartbeat]SetupSerial 3\n");
        return -1; 
    }
    return 1; 
}
/*SerialJNI_open*/
static int serial_open(void)
{
    int ret; 
    printf("serial_open\n");

    fd_serial = open(HEARTBEAT_SERIAL_FILE, O_RDWR); 
    if (fd_serial > 0) {
        serial_set_speed(fd_serial, 57600); 
        ret = serial_set_parity(fd_serial, 8, 'n', 1); 
        if (ret)
            return ret; 
        
        return 0; 
    } else {
        printf("serial_file failed to open path:%s\n", HEARTBEAT_SERIAL_FILE);
        return -errno; 
    }
}
/*SerialJNI_close*/
static void serial_close(void)
{
        printf("[heartbeat]serial_close\n");  
    if (close(fd_serial)) {
        printf("[heartbeat]serial_close failed!\n");  
    }

    fd_serial = 0; 
}
#endif

const int write_file(char const* path, int value)
{
    printf("[heartbeat]write_file\n"); 
    int fd = open(path, O_WRONLY);
    if (fd >= 0) {
        char buffer[20]; 
        int bytes = sprintf(buffer, "%d\n", value);
        int amt = write(fd, buffer, bytes);
        close(fd);
    printf("[heartbeat]write_file:fd=%d\n", fd); 
        return amt == -1 ? -errno : 0; 
    } else {
        printf("write_file failed to open path:%s\n", path);
        return -errno; 
    }
}

const int read_file(char const* path)
{
    char buf[500] = {0}; 
    int ret; 
    int fd = open(path, O_RDONLY);

    printf("[heartbeat]read_file\n"); 
    if (fd >= 0) {
        while (1) {
            printf("[heartbeat]read_file:fd=%d\n", fd); 
            ret = read(fd, buf, sizeof(buf)/sizeof(buf[0])); 
            if (ret <= 0) {
                printf("read_file failed to read path:%s\n", path);
            } else
                printf("[heartbeat]read_serial:%s\n", buf); 
        }
        close(fd); 
    } else {
        printf("read_file failed to open path:%s\n", path);
        return -errno; 
    }

}
static int heartbeat_power_on(int power_state)
{
    int err = 0; 
    printf("[heartbeat]heartbeat_power_on\n"); 
    err = write_file(HEARTBEAT_POWER_ON_FILE, power_state); 
    return err; 
}

static int heartbeat_reset(int reset_state)
{
    int err = 0; 
    printf("[heartbeat]heartbeat_reset\n"); 
    err = write_file(HEARTBEAT_RESET_FILE, reset_state); 
    return err; 
}

const int write_serial(char const* path, int *value)
{
    printf("[heartbeat]write_file\n"); 
    int fd = open(path, O_WRONLY);
    if (fd >= 0) {
        int amt = write(fd, value, 20);
        close(fd);
    printf("[heartbeat]write_file:fd=%d\n", fd); 
        return amt == -1 ? -errno : 0; 
    } else {
        printf("write_file failed to open path:%s\n", path);
        return -errno; 
    }
}

static int PtestHeartBeat(int argc, char **argv)
{
    int i; 
    int ret; 
//    int value1[] = {0x55, 0x55, 0x55, 0x55, 0x55, 0x55, 0x55, 0x55, 0x55, 0x55, 0x55, 0x55, 0x55, 0x55, 0x55, 0x55, 0x55, 0x55, 0x55, 0x55}; 
    char value1[] = "abcdefghijklmnopqrstuvwxyz"; 
    int value2[20] = {0}; 
    int num_read; 
    int insert_pos = 0;
    int check_pos = 0;
    int heart_num = 0;
    char xfer_buf[1];
    char test_buf[1024];
    char check_pass_len = 126;
    char check_heart_rate = 0;
    char check_comm = 0;
    char check_state = 0;

      int flags;
        printf("[heartbeat]test serial\n"); 
        ret = serial_open();
    flags = fcntl(ret, F_GETFL, 0);
    fcntl(ret, F_SETFL, flags | O_NONBLOCK);
        heartbeat_power_on(1);
        heartbeat_reset(0);
    sleep(1);
    num_read = read(fd_serial,(void *)test_buf, 1024);
    if(num_read > 1)
      {
        printf("okokok %d\r\n", num_read);
        printf("%s", test_buf);
        flags = fcntl(ret, F_GETFL, 0);
        fcntl(ret, F_SETFL, flags & ~O_NONBLOCK);

            heartbeat_power_on(0);
            serial_close();
            return 1;
      }
    else
      {
        printf("no command\r\n");
            heartbeat_power_on(0);
            serial_close(); 
        return 0;
      }
        if (ret) {
            while (1) {
          memset(xfer_buf, 0, 1);
                num_read = read(fd_serial, (void *)xfer_buf, 1); 
                //printf("[heartbeat]read tty port string data\n"); 
                //for (i=0; i<num_read; i++) {
          // printf("%c, ", xfer_buf[i]);
                //}
        //                printf("\r\n ");
//                printf("[heartbeat]read tty port raw data len:%d\n", num_read);
        if(num_read < 0)
          {
                     printf("num_read < 0\r\n");
          }
        else
          {
//                        printf("%02x", xfer_buf[0]);
                        if(check_pass_len > 0)
                                check_pass_len--;
                        if(check_state==0 && check_comm==0 && check_heart_rate==0)
                        {
                                if(xfer_buf[0] == 0xaa)
                                {
                                        check_state = 1;
                                        check_pass_len = 1;
                                }
                        }
                        else if(check_pass_len == 0)
                        {
                                if(check_state == 1)
                                {
                                        if(xfer_buf[0] == 0x04)
                                        {
                                                heart_num++;
                                                check_pass_len = 8;
                                        }
                                        else if(xfer_buf[0] == 0x12)
                                        {
                                                printf("heart num : %d\r\n", heart_num);
                                                heart_num = 0;
                                                check_pass_len = 2;
                                                check_state = 0;
                                                check_comm = 1;
                                        }
                                        else
                                        {
                                                printf("no way : %02x\r\n", xfer_buf[0]);
                                        }
                                }
                                else if(check_comm == 1)
                                {
                                        printf("communication %02x\r\n", xfer_buf[0]);
                                        check_comm = 0;
                                        check_pass_len = 2;
                                        check_heart_rate = 1;
                                }
                                else if(check_heart_rate == 1)
                                {
                                        printf("heart rate : %d\r\n", xfer_buf[0]);
                                        check_heart_rate = 0;
                                        check_pass_len = 18;
                                        check_state = 1;
                                }
                        }
/*
                for (i=0; i<num_read; i++)
                        {
                                printf("%02x ", xfer_buf[i]);
                        }
*/
//                        printf("\r\n "); 
                        
          }
          //      read_file(HEARTBEAT_SERIAL_FILE);
            }
        serial_close(); 
    }

    return 0;
}

static int CtestHeartBeat(int argc, char **argv)
{
    int i; 
    int ret; 
//    int value1[] = {0x55, 0x55, 0x55, 0x55, 0x55, 0x55, 0x55, 0x55, 0x55, 0x55, 0x55, 0x55, 0x55, 0x55, 0x55, 0x55, 0x55, 0x55, 0x55, 0x55}; 
    char value1[] = "abcdefghijklmnopqrstuvwxyz"; 
    int value2[20] = {0}; 
    int num_read; 
    int insert_pos = 0;
    int check_pos = 0;
    int heart_num = 0;
    char xfer_buf[1];
    char test_buf[1024];
    char check_pass_len = 126;
    char check_heart_rate = 0;
    char check_comm = 0;
    char check_state = 0;

      int flags;
        printf("[heartbeat]test serial\n"); 
        ret = serial_open();
    flags = fcntl(ret, F_GETFL, 0);
    fcntl(ret, F_SETFL, flags | O_NONBLOCK);
        heartbeat_power_on(1);
        heartbeat_reset(0);
    sleep(1);
    num_read = read(fd_serial,(void *)test_buf, 1024);
    if(num_read > 1)
      {
        printf("okokok %d\r\n", num_read);
        printf("%s", test_buf);
        flags = fcntl(ret, F_GETFL, 0);
        fcntl(ret, F_SETFL, flags & ~O_NONBLOCK);
      }
    else
      {
        printf("no command\r\n");
            __android_log_print(4, LOG_TAG, "No data from Heartbeat chip!");
            return -2;
      }
        if (ret) {
            while (1) {
          memset(xfer_buf, 0, 1);
                num_read = read(fd_serial, (void *)xfer_buf, 1); 
                //printf("[heartbeat]read tty port string data\n"); 
                //for (i=0; i<num_read; i++) {
          // printf("%c, ", xfer_buf[i]);
                //}
        //                printf("\r\n ");
//                printf("[heartbeat]read tty port raw data len:%d\n", num_read);
        if(num_read < 0)
          {
                     printf("num_read < 0\r\n");
          }
        else
          {
//                        printf("%02x", xfer_buf[0]);
                        if(check_pass_len > 0)
                                check_pass_len--;
                        if(check_state==0 && check_comm==0 && check_heart_rate==0)
                        {
                                if(xfer_buf[0] == 0xaa)
                                {
                                        check_state = 1;
                                        check_pass_len = 1;
                                }
                        }
                        else if(check_pass_len == 0)
                        {
                                if(check_state == 1)
                                {
                                        if(xfer_buf[0] == 0x04)
                                        {
                                                heart_num++;
                                                check_pass_len = 8;
                                        }
                                        else if(xfer_buf[0] == 0x12)
                                        {
                                                printf("heart num : %d\r\n", heart_num);
                                                heart_num = 0;
                                                check_pass_len = 2;
                                                check_state = 0;
                                                check_comm = 1;
                                        }
                                        else
                                        {
                                                printf("no way : %02x\r\n", xfer_buf[0]);
                                        }
                                }
                                else if(check_comm == 1)
                                {
                                        printf("communication %02x\r\n", xfer_buf[0]);
                                        check_comm = 0;
                                        check_pass_len = 2;
                                        check_heart_rate = 1;
                                        if (xfer_buf[0] == 0xC8) {
                                            heartbeat_power_on(0);
                                            serial_close(); 
                                            return 1;
                                        } else if (xfer_buf[0] == 0x00) {
                                            heartbeat_power_on(0);
                                            serial_close(); 
                                            return -1;
                                        } else {
                                            __android_log_print(4, LOG_TAG, "read flag has exception!");
                                            return -3;
                                        }

                                }
                                else if(check_heart_rate == 1)
                                {
                                        printf("heart rate : %d\r\n", xfer_buf[0]);
                                        check_heart_rate = 0;
                                        check_pass_len = 18;
                                        check_state = 1;
                                }
                        }
/*
                for (i=0; i<num_read; i++)
                        {
                                printf("%02x ", xfer_buf[i]);
                        }
*/
//                        printf("\r\n "); 
                        
          }
          //      read_file(HEARTBEAT_SERIAL_FILE);
            }
        serial_close(); 
    }

    return 0;
}

//modify for add heartbeatforPCBA by xiasiping 20140925 end

static int
write_string(char const* path, char value[])
{
    int fd;
    static int already_warned = 0;
    fd = open(path, O_RDWR);

    if (fd >= 0) {
        int length = strlen(value);
        int amt = write(fd, value, length);
        close(fd);
        return amt == -1 ? -errno : 0;
    } else {
        if (already_warned == 0) {
            already_warned = 1;
        }
        return -errno;
    }
}

static int
write_int(char const* path, int value)
{
    int fd;
    static int already_warned = 0;

    fd = open(path, O_RDWR);
    if (fd >= 0) {
        char buffer[20];
        int bytes = sprintf(buffer, "%d\n", value);
        int amt = write(fd, buffer, bytes);
        close(fd);
        return amt == -1 ? -errno : 0;
    } else {
        if (already_warned == 0) {

            already_warned = 1;
        }
        return -errno;
    }
}

static void flashlightControl
(JNIEnv *env, jobject obj, jstring value)
{        
    int retr;
    AddRootPermission();
    const char *vre = (*env)->GetStringUTFChars(env,value,0);
    retr = write_string("/sys/class/leds/torch/brightness",vre);
    (*env)->ReleaseStringUTFChars(env,value,vre);
}

//Modify for CIT optimization by xiasiping 20140730 start
static void flashlightControl_d
(JNIEnv *env, jobject obj, jstring value, jstring str)
{
    int retr;
    AddRootPermission();
    const char *vre = (*env)->GetStringUTFChars(env,value,0);
    const char *vres = (*env)->GetStringUTFChars(env,str,0);
    retr = write_string(vres, vre);
    (*env)->ReleaseStringUTFChars(env,value,vre);
}
//Modify for CIT optimization by xiasiping 20140730 end

static void buttonlightControl
(JNIEnv *env, jobject obj, jint b)
{       
    AddRootPermission();
    __android_log_print(4, LOG_TAG, "start exec buttonlightControl");

    write_int("/sys/class/leds/button_backlight/brightness", b);
}

//Modify for CIT optimization by xiasiping 20140730 start
static void buttonlightControl_d
(JNIEnv *env, jobject obj, jint b, jstring str)
{
    AddRootPermission();

    const char *vres = (*env)->GetStringUTFChars(env,str,0);

    __android_log_print(4, LOG_TAG, "start exec buttonlightControl");

    write_int(vres, b);
}
//Modify for CIT optimization by xiasiping 20140730 end

static void lightControl
(JNIEnv *env, jobject obj, jint b)
{       
    AddRootPermission();

    int retr;
    int retb;
    int retg;

    if(b == 0){
        retr = write_string("/sys/class/leds/red/brightness", "40");
        retb = write_string("/sys/class/leds/orange/brightness", "0");
        retg = write_string("/sys/class/leds/green/brightness", "0");
    }else if(b == 1){
        retr = write_string("/sys/class/leds/red/brightness", "0");
        retb = write_string("/sys/class/leds/orange/brightness", "40");
        retg = write_string("/sys/class/leds/green/brightness", "0");
    }else if(b == 2){
        retr = write_string("/sys/class/leds/red/brightness", "0");
        retb = write_string("/sys/class/leds/orange/brightness", "0");
        retg = write_string("/sys/class/leds/green/brightness", "40");
    }else{
        retr = write_string("/sys/class/leds/red/brightness", "0");
        retb = write_string("/sys/class/leds/orange/brightness", "0");
        retg = write_string("/sys/class/leds/green/brightness", "0");
    }
    __android_log_print(4, LOG_TAG, "retr = %d\n", retr);
    __android_log_print(4, LOG_TAG, "retb = %d\n", retb);
    __android_log_print(4, LOG_TAG, "retg = %d\n", retg);
}

//Modify for CIT optimization by xiasiping 20140730 start
static void lightControl_d
(JNIEnv *env, jobject obj, jint b, jstring str_r, jstring str_o, jstring str_g)
{
    AddRootPermission();

    int retr;
    int retb;
    int retg;
    const char *vrer = (*env)->GetStringUTFChars(env,str_r,0);
    const char *vreg = (*env)->GetStringUTFChars(env,str_g,0);
    const char *vreb = (*env)->GetStringUTFChars(env,str_o,0);

    if(b == 0){
        retr = write_string( vrer, "40");
        retb = write_string( vreb, "0");
        retg = write_string( vreg, "0");
    }else if(b == 2){
        retr = write_string( vrer, "0");
        retb = write_string( vreb, "40");
        retg = write_string( vreg, "0");
    }else if(b == 1){
        retr = write_string( vrer, "0");
        retb = write_string( vreb, "0");
        retg = write_string( vreg, "40");
    }else{
        retr = write_string( vrer, "0");
        retb = write_string( vreb, "0");
        retg = write_string( vreg, "0");
    }
    __android_log_print(4, LOG_TAG, "retr = %d\n", retr);
    __android_log_print(4, LOG_TAG, "retb = %d\n", retb);
    __android_log_print(4, LOG_TAG, "retg = %d\n", retg);
}
//Modify for CIT optimization by xiasiping 20140730 end

static void lightControlForIS
(JNIEnv *env, jobject obj, jint b)
{
    AddRootPermission();

    int retr;
    int retb;
    int retg;

    if(b == 0){
        retr = write_string("/sys/class/leds/red/brightness", "200");
        retb = write_string("/sys/class/leds/orange/brightness", "0");
        retg = write_string("/sys/class/leds/green/brightness", "0");
    }else if(b == 1){
        retr = write_string("/sys/class/leds/red/brightness", "0");
        retb = write_string("/sys/class/leds/orange/brightness", "245");
        retg = write_string("/sys/class/leds/green/brightness", "0");
    }else if(b == 2){
        retr = write_string("/sys/class/leds/red/brightness", "0");
        retb = write_string("/sys/class/leds/orange/brightness", "0");
        retg = write_string("/sys/class/leds/green/brightness", "200");
    }else{
        retr = write_string("/sys/class/leds/red/brightness", "0");
        retb = write_string("/sys/class/leds/orange/brightness", "0");
        retg = write_string("/sys/class/leds/green/brightness", "0");
    }
    __android_log_print(4, LOG_TAG, "retr = %d\n", retr);
    __android_log_print(4, LOG_TAG, "retb = %d\n", retb);
    __android_log_print(4, LOG_TAG, "retg = %d\n", retg);
}

static void backlightControl
(JNIEnv *env, jobject obj, jint b)
{
    AddRootPermission();
    __android_log_print(4, LOG_TAG, "start exec backlightControl");

    write_int("/sys/class/leds/lcd-backlight/brightness", b);
}

static int calibration
(JNIEnv *env, jobject obj)
{
    __android_log_print(4, LOG_TAG, "calibration start");
    AddRootPermission();
    int fd;
    int result;
    int hfile;

    fd = open("/dev/bma222", O_RDWR);
    __android_log_print(4, LOG_TAG, "open /dev/bma222 fd = %d\n",fd);
    if(0 <= fd)
    {
        result = ioctl(fd, BMA222_SET_OFFSET_RESET,NULL);
        //Dataxyz was needed because I don't know the way to store the caldata
        __android_log_print(4, LOG_TAG, "BMA222 BMA222_SET_OFFSET_RESET result: %d\n", result);
        if(0 > result)
        {
            return -1;
        }

        close(fd);

        return 0;
    }

    fd = open("/dev/bma250", O_RDWR);
    __android_log_print(4, LOG_TAG, "open /dev/bma250 fd = %d\n",fd);
    if(0 <= fd)
    {
        result=ioctl(fd,BMA250_SET_OFFSET_RESET,NULL);
        __android_log_print(4, LOG_TAG, "BMA250 BMA250_SET_OFFSET_RESET result: %d\n", result);
        if(0 > result)
        {
            return -1;
        }

        close(fd);

        return 0;
    }
    __android_log_print(4, LOG_TAG, "calibration end");
    return -1;
}


static jfloatArray getMotionXYZ
(JNIEnv *env, jobject obj)

{
    __android_log_print(4, LOG_TAG, "getMotionXYZ start");
    AddRootPermission();
    int fd;
    int result;
    static jfloat values[] = {0,0,0};
    jfloatArray array =  (*env)->NewFloatArray(env,3);

    fd = open("/dev/bma250", O_RDWR);
    __android_log_print(4, LOG_TAG, "open /dev/bma250 fd = %d\n",fd);
    if(0 <= fd){
        bma250acc_t     Dataxyz;
        result = ioctl(fd, BMA250_READ_ACCEL_XYZ, &Dataxyz);
        __android_log_print(4, LOG_TAG, "open /dev/bma250 fd = %d\n",fd);
        __android_log_print(4, LOG_TAG, "bma250 readXYZ [X %d] [Y %d] [Z %d] \n", Dataxyz.x, Dataxyz.y, Dataxyz.z);
        values[0] = -(Dataxyz.x * (9.8/256));
        values[1] = -(Dataxyz.y * (9.8/256));
        values[2] = -(Dataxyz.z * (9.8/256));
        __android_log_print(4, LOG_TAG, "values[0] = %f", values[0]);
        __android_log_print(4, LOG_TAG, "values[1] = %f", values[1]);
        __android_log_print(4, LOG_TAG, "values[2] = %f", values[2]);
        close(fd);
        __android_log_print(4, LOG_TAG, "close /dev/bma250");
    }


    (*env)->SetFloatArrayRegion(env,array,0,3,values);
    __android_log_print(4, LOG_TAG, "getMotionXYZ end");
    return array;

}

static jintArray getCompassValues
(JNIEnv *env, jobject obj)
{
    __android_log_print(4, LOG_TAG, "getCompassValues start");
    AddRootPermission();
    int fd;
    int result;
    static short mag[3] = {0};

    fd = open("/dev/akm8975_dev", O_RDWR);
    __android_log_print(4, LOG_TAG, "open /dev/akm8975_dev fd = %d\n",fd);
    result = ioctl(fd, ECS_IOCTL_GET_XYZ, mag);
    int val[3] = {(int)mag[0], (int)mag[1], (int)mag[2]};
    __android_log_print(4, LOG_TAG, "akm8975_dev Read result: %d\n", result);
    __android_log_print(4, LOG_TAG, "val[0] = %d", val[0]);
    __android_log_print(4, LOG_TAG, "val[1] = %d", val[1]);
    __android_log_print(4, LOG_TAG, "val[2] = %d", val[2]);

    jintArray array =  (*env)->NewIntArray(env,3);
    (*env)->SetIntArrayRegion(env,array,0,3,val);
    close(fd);
    __android_log_print(4, LOG_TAG, "close /dev/akm8975_dev");
    __android_log_print(4, LOG_TAG, "getCompassValues end");
    return array;
}

static jintArray getOrientationValues
(JNIEnv *env, jobject obj)
{
    __android_log_print(4, LOG_TAG, "getOrientationValues start");
    AddRootPermission();
    int fd;
    int result;

    int flag = 1;
    int mag[16] = {0};

    fd = open("/dev/akm8975_dev", O_RDWR);
    __android_log_print(4, LOG_TAG, "open /dev/akm8975_dev fd = %d\n",fd);


    result = ioctl(fd, ECS_IOCTL_GET_YPR, mag);
    __android_log_print(4, LOG_TAG, "ECS_IOCTL_GET_YPR result: %d\n", result);
    int i;
    for(i = 0; i < 16; i++)
    {
        __android_log_print(4, LOG_TAG, "mag = %d\n", mag[i]);
    }
    jintArray array =  (*env)->NewIntArray(env,16);
    (*env)->SetIntArrayRegion(env,array,0,16,mag);
    close(fd);
    __android_log_print(4, LOG_TAG, "close /dev/akm8975_dev");
    __android_log_print(4, LOG_TAG, "getOrientationValues end");
    return array;
}

static jstring proximityDistance
(JNIEnv *env, jobject obj)
 {
           AddRootPermission();
           int handle = open("/sys/class/misc/cm3623_ps/cal_ps_data", O_RDONLY);
           char val[4]={0};
           int iret = 0;
           int test =55;
           if(handle == -1)
           {      
                    __android_log_print(4, LOG_TAG, "open the cal_ps_data failed");
           }else
           {
              iret = read(handle,val,4);
                   val[3] = '\0';
              __android_log_print(4, LOG_TAG, "iret = %d,val = %s",iret,val);
           }
           close(handle);

           return (*env)->NewStringUTF(env,val);
  }

static jstring lightDegree
  (JNIEnv *env, jobject obj)
  {   
        AddRootPermission();
        int handle = open("/sys/class/misc/cm3623_als/raw_als_data", O_RDONLY);
        char val[10]={0};
        int iret = 0;
        if(handle == -1)
      {

         __android_log_print(4, LOG_TAG, "open the raw_ps_data failed");
      }else
      {
         iret = read(handle,val,10);
        val[9] = '\0';
         __android_log_print(4, LOG_TAG, "iret = %d,val = %s",iret,val);
      }
      close(handle);
      return (*env)->NewStringUTF(env,val);
  }

//wifi
/*static jint startWifiTestMode
(JNIEnv *env, jobject obj)
{      
        int iRet = 0;
        __android_log_print(4, LOG_TAG, "[startWifiTestMode] +");
         AddRootPermission();
       iRet = BCM_Start_TestMode();
       __android_log_print(4, LOG_TAG, "[startWifiTestMode] the iRet = %d",iRet);
        __android_log_print(4, LOG_TAG, "[startWifiTestMode] -");
       return iRet;
}
static jint stopWifiTestMode
(JNIEnv *env, jobject obj)
{      
        int iRet = 0;
        __android_log_print(4, LOG_TAG, "[stopWifiTestMode] +");
         AddRootPermission();
       iRet = BCM_Stop_TestMode();
       __android_log_print(4, LOG_TAG, "[stopWifiTestMode] the iRet = %d",iRet);
        __android_log_print(4, LOG_TAG, "[stopWifiTestMode] -");
       return iRet;
}
static jint InitWifiTestMode
(JNIEnv *env, jobject obj)
{      
        int iRet = 0;
        __android_log_print(4, LOG_TAG, "[InitWifiTestMode] +");
        AddRootPermission();
       iRet = Init_Wifi_TestMode();
       __android_log_print(4, LOG_TAG, "[InitWifiTestMode] the iRet = %d",iRet);
        __android_log_print(4, LOG_TAG, "[InitWifiTestMode] -");
       return iRet;
}
static jint deinitWifiTestMode
(JNIEnv *env, jobject obj)
{       
        int iRet = 0;
        __android_log_print(4, LOG_TAG, "[btduttest] +");
        AddRootPermission();
      iRet = Deinit_Wifi_TestMode();
       __android_log_print(4, LOG_TAG, "[deinitWifiTestMode] the iRet = %d",iRet);
      return iRet;
}
static jint setWifiMode
(JNIEnv *env, jobject obj,jint iMode,jint iRate)
{      
        int iRet = 0;
        __android_log_print(4, LOG_TAG, "[setWifiMode] +");
        AddRootPermission();
       iRet = BCM_Set_Wifi_Mode(iMode,iRate);
       __android_log_print(4, LOG_TAG, "[setWifiMode] the iRet = %d",iRet);
       __android_log_print(4, LOG_TAG, "[setWifiMode] -");
       return iRet;
}

static jint wifiStartRx
(JNIEnv *env, jobject obj,jint iChannel)
{       
        int iRet = 0;
        __android_log_print(4, LOG_TAG, "[wifiStartRx] +");
        AddRootPermission();
        iRet = BCM_Start_Rx(iChannel);  
       __android_log_print(4, LOG_TAG, "[wifiStartRx] the iRet = %d",iRet);
        __android_log_print(4, LOG_TAG, "[wifiStartRx] -");
        return iRet;
}
static jint setRxEquipmentType
(JNIEnv *env, jobject obj,jint iType)
{       
        int iRet = 0;
        __android_log_print(4, LOG_TAG, "[setRxEquipmentType] +");

        AddRootPermission();
        BCM_Set_Equ_Type(iType);  
        __android_log_print(4, LOG_TAG, "[setRxEquipmentType] -");
        return iRet;
}
static jint setRxTotalPacket
(JNIEnv *env, jobject obj,jint iTotalPacket)
{       
        int iRet = 0;
        __android_log_print(4, LOG_TAG, "[setRxTotalPacket] +");
        AddRootPermission();
        BCM_Rx_TotalPacket(iTotalPacket);  
        __android_log_print(4, LOG_TAG, "[setRxTotalPacket] -");
        return iRet;
}
static jint wifiStopRx
(JNIEnv *env, jobject obj)
{      
        int iRet = 0;
        __android_log_print(4, LOG_TAG, "[wifiStopRx] +");
        AddRootPermission();
       iRet = BCM_Stop_Rx();
       __android_log_print(4, LOG_TAG, "[wifiStopRx] the iRet = %d",iRet);
       __android_log_print(4, LOG_TAG, "[wifiStopRx] -");
       return iRet;
}

static jintArray readwifiRxResult
(JNIEnv *env, jobject obj)
{       
        __android_log_print(4, LOG_TAG, "[readwifiRxResult] +");
        AddRootPermission();
        static jint reValue[] = {0,0,0};
        int TotalPackets = 0;
        int per = 0;


        int nRet = BCM_Read_Rx_Result(&TotalPackets,&per);
        jintArray array =  (*env)->NewIntArray(env,3);
        reValue[0] = nRet;
        reValue[1] = TotalPackets;
        reValue[2] = per;
 
        (*env)->SetIntArrayRegion(env,array,0,3,reValue);
         __android_log_print(4, LOG_TAG, "[readwifiRxResult] -");
       return array;
}
static jint readWifiRxRssi
(JNIEnv *env, jobject obj)
{        
        int iRet = 0;
         AddRootPermission();
        iRet = BCM_Read_Rx_RSSI();
        __android_log_print(4, LOG_TAG, "[readWifiRxRssi] the iRet = %d",iRet);
        return iRet;
}
static jint wifiStartTx
(JNIEnv *env, jobject obj,jint iChannel)
{      
        int iRet = 0;
        __android_log_print(4, LOG_TAG, "[wifiStartTx] +");
       AddRootPermission();
       iRet = BCM_Start_Tx(iChannel);
        __android_log_print(4, LOG_TAG, "[wifiStartTx] the iRet = %d",iRet);
       __android_log_print(4, LOG_TAG, "[wifiStartTx] -");
       return iRet;
}
static jint wifiStopTx
(JNIEnv *env, jobject obj)
{      
        int iRet = 0;
        __android_log_print(4, LOG_TAG, "[wifiStopTx] +");
        AddRootPermission();
        iRet = BCM_Stop_Tx();
        __android_log_print(4, LOG_TAG, "[wifiStopTx] the iRet = %d",iRet);
        __android_log_print(4, LOG_TAG, "[wifiStopTx] -");
       return iRet;
}
static jint wifiSetTxGain
(JNIEnv *env, jobject obj,jint iGain)
{      
        int iRet = 0;
       AddRootPermission();
       iRet = SetWifiTxGain(iGain);
        __android_log_print(4, LOG_TAG, "[wifiSetTxGain] the iRet = %d",iRet);
       return iRet;
}
static jint wifiSetTxSIFS
(JNIEnv *env, jobject obj,jint iSifs)
{      
        int iRet = 0;
       AddRootPermission();
       iRet = SetWifiTxSIFS(iSifs);
        __android_log_print(4, LOG_TAG, "[wifiSetTxSIFS] the iRet = %d",iRet);
       return iRet;
}
static jint wifiSetTxNumberofFrames
(JNIEnv *env, jobject obj,jint nFrames)
{      
        int iRet = 0;
       AddRootPermission();
       iRet = SetWifiTxNumberofFrames(nFrames);
        __android_log_print(4, LOG_TAG, "[wifiSetTxNumberofFrames] the iRet = %d",iRet);
       return iRet;
}
static jint wifiSetPayLoadLength
(JNIEnv *env, jobject obj,jint nPayLoad)
{      
        int iRet = 0;
       AddRootPermission();
       iRet = SetWifiTxPayloadLength(nPayLoad);
        __android_log_print(4, LOG_TAG, "[wifiSetPayLoadLength] the iRet = %d",iRet);
       return iRet;
}
static jint wifiSetPreamble
(JNIEnv *env, jobject obj,jint nPreamble)
{      
        int iRet = 0;
       AddRootPermission();
       iRet = SetPreamble(nPreamble);
        __android_log_print(4, LOG_TAG, "[wifiSetPreamble] the iRet = %d",iRet);
       return iRet;
}

static jint getWifiStatus
(JNIEnv *env, jobject obj)
{        
        int iRet = 0;
         __android_log_print(4, LOG_TAG, "[getWifiStatus] +");
        AddRootPermission();
       int iRate = 0;
       iRate = BCM_GetWiFi_Status();
       __android_log_print(4, LOG_TAG, "[getWifiStatus] the iRate = %d",iRate);
       __android_log_print(4, LOG_TAG, "[getWifiStatus] -");
       return iRate;
}
static jintArray readwifiRxStatus
(JNIEnv *env, jobject obj)
{       
        __android_log_print(4, LOG_TAG, "[readwifiRxStatus] +");
         AddRootPermission();
        static jint reValue[] = {0,0,0};
        int TotalPackets = 0;
        int per = 0;


        int nRet = BCM_Read_Rx_Result(&TotalPackets,&per);
        jintArray array =  (*env)->NewIntArray(env,3);
        reValue[0] = nRet;
        reValue[1] = TotalPackets;
        reValue[2] = per;
 
        (*env)->SetIntArrayRegion(env,array,0,3,reValue);
        __android_log_print(4, LOG_TAG, "[readwifiRxStatus] -");
        return array;
}
static jint wifiClearRxStatus
(JNIEnv *env, jobject obj)
{      
        int iRet = 0;
        AddRootPermission();
       iRet = BCM_Clear_Rx_Status();
       __android_log_print(4, LOG_TAG, "[wifiClearRxStatus] the iRet = %d",iRet);
       return iRet;
}

static jint wifiGetTxChannel
(JNIEnv *env, jobject obj)
{
        int iRet = 0;
//       iRet = BCM_Get_Tx_Channel();
//       SetWifiTxGain(100);
       __android_log_print(4, LOG_TAG, "[wifiGetTxChannel] the iRet = %d",iRet);
       return iRet;
}
//jstring to char*
char* jstringTostring(JNIEnv* env, jstring jstr)
{        
char* rtn = NULL;
jclass clsstring = (*env)->FindClass(env,"java/lang/String");
jstring strencode = (*env)->NewStringUTF(env,"utf-8");
jmethodID mid = (*env)->GetMethodID(env,clsstring, "getBytes", "(Ljava/lang/String;)[B");
jbyteArray barr= (jbyteArray)(*env)->CallObjectMethod(env,jstr, mid, strencode);
jsize alen = (*env)->GetArrayLength(env,barr);
jbyte* ba = (*env)->GetByteArrayElements(env,barr, JNI_FALSE);
if (alen > 0)
{
rtn = (char*)malloc(alen + 1);
memcpy(rtn, ba, alen);
rtn[alen] = 0;
}
 (*env)->ReleaseByteArrayElements(env,barr, ba, 0);
return rtn;
}

//char* to jstring
jstring stoJstring(JNIEnv* env, const char* pat)
{
jclass strClass = (*env)->FindClass(env,"Ljava/lang/String;");
jmethodID ctorID = (*env)->GetMethodID(env,strClass, "<init>", "([BLjava/lang/String;)V");
jbyteArray bytes = (*env)->NewByteArray(env,strlen(pat));
(*env)->SetByteArrayRegion(env,bytes, 0, strlen(pat), (jbyte*)pat);
jstring encoding = (*env)->NewStringUTF(env,"utf-8");
return (jstring)(*env)->NewObject(env,strClass, ctorID, bytes, encoding);
} 

static jstring getWifiMacAddress
(JNIEnv *env, jobject obj)
{      
       int iRet = 0;
       AddRootPermission();
       char c[25] = {0};
       iRet = BCM_Get_Addr(c);
       __android_log_print(4, LOG_TAG, "[getWifiMacAddress] the iRet = %d,c = %s",iRet,c);
       return (*env)->NewStringUTF(env,c);
}
static jint setWifiMacAddress
(JNIEnv *env, jobject obj,jstring strMacAddress)
{      
        
       int iRet = 0;
//       iRet = BCM_Set_Addr(jstringTostring(env,strMacAddress));
//        char cMacAddress[15] = {0};
        const char *cMacAddress = (*env)->GetStringUTFChars(env,strMacAddress,0);
        iRet = BCM_Set_Addr(cMacAddress);
        (*env)->ReleaseStringUTFChars(env,strMacAddress,cMacAddress);
        
       __android_log_print(4, LOG_TAG, "[setWifiMacAddress] the iRet = %d",iRet);
       return 0;
}

static void setCarrierSuppressionMode
(JNIEnv *env, jobject obj, jint enable)
{
    __android_log_print(4, LOG_TAG, "setCarrierSuppressionMode start");
    AddRootPermission();
    SetCarrierSuppressionMode(enable);
    __android_log_print(4, LOG_TAG, "setCarrierSuppressionMode end");
}

static int runCommand
(JNIEnv *env, jobject obj, jstring cmd)
{
    int iRet;
    __android_log_print(4, LOG_TAG, "runCommand start");
    AddRootPermission();
    const char *charcmd = (*env)->GetStringUTFChars(env,cmd,0);
    iRet = RunCommand(charcmd);
    (*env)->ReleaseStringUTFChars(env,cmd,charcmd);
    __android_log_print(4, LOG_TAG, "[runCommand] the iRet = %d",iRet);
    __android_log_print(4, LOG_TAG, "runCommand end");
    return iRet;
}*/


         
static jint getVoltage
(JNIEnv *env, jobject obj)
{
    AddRootPermission();

    int fd;
    int result;
    char buf[5] = {0};

    fd = open("/sys/class/power_supply/battery/voltage_now", O_RDONLY, 0);
    __android_log_print(4, LOG_TAG, "fd = %d", fd);

    result = read(fd, buf, 4);
    __android_log_print(4, LOG_TAG, "result = %d", result);
    jint value = atoi(buf);
    __android_log_print(4, LOG_TAG, "value = %d", value);

    close(fd);

    __android_log_print(4, LOG_TAG, "buf = %s", buf);
    return value;
}  

//Modify for CIT optimization by xiasiping 20140730 start
static jint getVoltage_d
(JNIEnv *env, jobject obj, jstring str)
{
    AddRootPermission();
    const char *vres = (*env)->GetStringUTFChars(env,str,0);
    int fd;
    int result;
    char buf[5] = {0};

    fd = open(vres, O_RDONLY, 0);
    __android_log_print(4, LOG_TAG, "fd = %d", fd);

    result = read(fd, buf, 4);
    __android_log_print(4, LOG_TAG, "result = %d", result);
    jint value = atoi(buf);
    __android_log_print(4, LOG_TAG, "value = %d", value);

    close(fd);

    __android_log_print(4, LOG_TAG, "buf = %s", buf);
    return value;
} 
//Modify for CIT optimization by xiasiping 20140730 end

//Add for get temperature of battery by lvhongshan 20140222 start
static jint getTemp
(JNIEnv *env, jobject obj)
{
    AddRootPermission();

    int fd;
    int result;
    char buf[4];

    fd = open("/sys/class/power_supply/battery/temp", O_RDONLY, 0);
    __android_log_print(4, LOG_TAG, "fd = %d", fd);

    result = read(fd, buf, 4);
    __android_log_print(4, LOG_TAG, "result = %d", result);
    jint value = atoi(buf);
    __android_log_print(4, LOG_TAG, "value = %d", value);

    close(fd);

    __android_log_print(4, LOG_TAG, "buf = %s", buf);
    return value;
}

//Modify for CIT optimization by xiasiping 20140730 start
static jint getTemp_d
(JNIEnv *env, jobject obj, jstring str)
{
    AddRootPermission();

    const char *vres = (*env)->GetStringUTFChars(env,str,0);

    int fd;
    int result;
    char buf[4];

    fd = open(vres, O_RDONLY, 0);
    __android_log_print(4, LOG_TAG, "fd = %d", fd);

    result = read(fd, buf, 4);
    __android_log_print(4, LOG_TAG, "result = %d", result);
    jint value = atoi(buf);
    __android_log_print(4, LOG_TAG, "value = %d", value);

    close(fd);

    __android_log_print(4, LOG_TAG, "buf = %s", buf);
    return value;
}
//Modify for CIT optimization by xiasiping 20140730 end
//Add for get temperature of battery by lvhongshan 20140222 end

static jint getCapacity
(JNIEnv *env, jobject obj)
{
    AddRootPermission();

    int fd;
    int result;
    char buf[4];

    fd = open("/sys/class/power_supply/gaugeIC/capacity", O_RDONLY, 0);
    __android_log_print(4, LOG_TAG, "fd = %d", fd);

    result = read(fd, buf, 4);
    __android_log_print(4, LOG_TAG, "result = %d", result);
    jint value = atoi(buf);
    __android_log_print(4, LOG_TAG, "value = %d", value);

    close(fd);

    __android_log_print(4, LOG_TAG, "buf = %s", buf);
    return value;
}        

static jint getHardwareVersion
(JNIEnv *env, jobject obj)
{
    AddRootPermission();

    int fd;
    int result;
    char buf[4];

    fd = open("/sys/class/power_supply/battery/batt-id", O_RDONLY, 0);
    __android_log_print(4, LOG_TAG, "fd = %d", fd);

    result = read(fd, buf, 4);
    __android_log_print(4, LOG_TAG, "result = %d", result);
    __android_log_print(4, LOG_TAG, "buf = %s", buf);
    jint value = atoi(buf);
    __android_log_print(4, LOG_TAG, "value = %d", value);

    close(fd);
    return value;
}  

//soft test
static jint wifiControl
(JNIEnv *env, jobject obj,jint a,jint b){

    AddRootPermission();

    void *handle;
    int (*wificit)(int,int);
    int val = 0;
    __android_log_print(4, LOG_TAG, "start exec dlopen");
    handle = dlopen("libbluedroid.so",RTLD_NOW);

    __android_log_print(4, LOG_TAG, "handle = %0x",handle);
    if(handle == NULL){
        __android_log_print(4, LOG_TAG,"dlopen failed: %s\n", dlerror());
        return -1;
    }

    __android_log_print(4, LOG_TAG, "dlsym exec start");
        wificit = dlsym(handle,"wificit");
    __android_log_print(4, LOG_TAG, "wificit exec start");
    val = wificit(a,b);
    __android_log_print(4, LOG_TAG, "val = %d",val);
    dlclose(handle);

    return (val);

}

#define HCIT_MAGIC 'h'

#define HCIT_IOCTL_BLUETOOTH_ON         _IOW(HCIT_MAGIC, HCITCMD_BLUETOOTH_ON, int)
#define HCIT_IOCTL_BLUETOOTH_OFF        _IOW(HCIT_MAGIC, HCITCMD_BLUETOOTH_OFF, int)

enum
{
    HCITCMD_DEFAULT = 0xA0,
    HCITCMD_KEYLED_ON = HCITCMD_DEFAULT,
    HCITCMD_KEYLED_OFF,
    HCITCMD_VIB_ON,
    HCITCMD_VIB_OFF,
    HCITCMD_CAMERA_FLASHLED_ON,
    HCITCMD_CAMERA_FLASHLED_OFF,
    HCITCMD_LCD_BACKLIGHT_ON,
    HCITCMD_LCD_BACKLIGHT_OFF,
    HCITCMD_GPIOLED_ON,
    HCITCMD_GPIOLED_OFF,
    HCITCMD_BLUETOOTH_ON,
    HCITCMD_BLUETOOTH_OFF,
    HCITCMD_MAX
};

static void bluetoothControl
(JNIEnv *env, jobject obj, jboolean b){
    AddRootPermission();
 //   int handle=open("/dev/hcit_misc",O_RDWR);
//    __android_log_print(4, LOG_TAG, "bluetoothControl = %d",handle);
    int ret;

    if(b)
    {
      ret = system("echo 1 > /sys/devices/platform/bt_power/rfkill/rfkill0/state");
        __android_log_print(4, LOG_TAG, "+++++open bluetooth ret = %d",ret);
    }
    else
    {
        ret = system("echo 0 > /sys/devices/platform/bt_power/rfkill/rfkill0/state");
        __android_log_print(4, LOG_TAG, "close bluetooth ret = %d",ret);
    }

//    close(handle);
}

static jint openBluetooth
(JNIEnv *env, jobject obj){

    AddRootPermission();

    void *handle;
    int (*bluetoothag)(void);
    int val = 0;
    __android_log_print(4, LOG_TAG, "start exec dlopen");
    handle = dlopen("libbluedroid.so",RTLD_NOW);

    __android_log_print(4, LOG_TAG, "handle = %0x",handle);
    if(handle == NULL){
        __android_log_print(4, LOG_TAG,"dlopen failed: %s\n", dlerror());
        return -1;
    }

    __android_log_print(4, LOG_TAG, "dlsym exec start");
    bluetoothag = dlsym(handle,"bluetoothcit");
    __android_log_print(4, LOG_TAG, "bluetoothcit exec start");
    val = bluetoothag();
    __android_log_print(4, LOG_TAG, "val = %d",val);
    dlclose(handle);

    return (val);
}


//##MotionCalibrate
static jintArray excemotcal
(JNIEnv *env, jobject obj)
{
    void *handle;
        static jint nValue[] = {0,0,0,0};
        static jint reValue[] = {0,0,0,0,0};
    int (*mot_cal_user)(int[]);
    int val = 0;
        __android_log_print(4, LOG_TAG, "start exec dlopen");
    handle = dlopen("mot_cal.so",RTLD_NOW);
       
        __android_log_print(4, LOG_TAG, "handle = %0x",handle);
    if(handle == NULL){
          __android_log_print(4, LOG_TAG,"dlopen failed: %s\n", dlerror());
          return (jintArray)-1;
    }
         
        __android_log_print(4, LOG_TAG, "dlsym exec start");
    mot_cal_user = dlsym(handle,"mot_cal_user");
        __android_log_print(4, LOG_TAG, "mot_cal exec start");
    val = mot_cal_user(nValue);
        

       jintArray array =  (*env)->NewIntArray(env,5);
       reValue[0] = val;
//       for(int i = 1;i < 5;i ++){
//            reValue[i] = nValue[i - 1];
//      }


       reValue[1] = nValue[0];
       reValue[2] = nValue[1];
       reValue[3] = nValue[2];
       reValue[4] = nValue[3];
        
       (*env)->SetIntArrayRegion(env,array,0,5,reValue);
        __android_log_print(4, LOG_TAG, "val = %d,x = %d,y = %d,z = %d,k = %d",val,nValue[0],nValue[1],nValue[2],nValue[3]);
    dlclose(handle);

    return array;
}
//##HW 校准
static jintArray execHcalibration
(JNIEnv *env, jobject obj)
{
    void *handle;

        static jint nValue[] = {0,0,0,0};
        static jint reValue[] = {0,0,0,0,0};

    int (*mot_cal_factory_Step2)(int[]);
    int val = 0;
    __android_log_print(4, LOG_TAG, "start exec dlopen");
    handle = dlopen("mot_cal.so",RTLD_NOW);

    __android_log_print(4, LOG_TAG, "handle = %0x",handle);
    if(handle == NULL){
        __android_log_print(4, LOG_TAG,"dlopen failed: %s\n", dlerror());
        return (jintArray)-1;
    }

           __android_log_print(4, LOG_TAG, "dlsym exec start");
    mot_cal_factory_Step2 = dlsym(handle,"mot_cal_factory_Step2");
    __android_log_print(4, LOG_TAG, "mot_cal exec start");
    val = mot_cal_factory_Step2(nValue);
    __android_log_print(4, LOG_TAG, "val = %d",val);
       jintArray array =  (*env)->NewIntArray(env,5);
       reValue[0] = val;
//       for(int i = 1;i < 5;i ++){
//            reValue[i] = nValue[i - 1];
//      }
       reValue[1] = nValue[0];
       reValue[2] = nValue[1];
       reValue[3] = nValue[2];
       reValue[4] = nValue[3];
        
       (*env)->SetIntArrayRegion(env,array,0,5,reValue);
        __android_log_print(4, LOG_TAG, "val = %d,x = %d,y = %d,z = %d,k = %d",val,nValue[0],nValue[1],nValue[2],nValue[3]);
    dlclose(handle);

    return array;
}
static jint execVcalibration
(JNIEnv *env, jobject obj)
{

    int hand=0;
    int fd=0;
    int result=0;

    bma250acc_t Dataxyz;
    AddRootPermission();
    __android_log_print(4, LOG_TAG,"AddRootPermission");

    fd = open("/dev/bma250", O_RDWR);

    if(fd>=0){

    __android_log_print(4, LOG_TAG,"open /dev/bma250 sucess");

    result = ioctl(fd, BMA250_READ_ACCEL_XYZ, &Dataxyz);

     //printf("bma250 readXYZ [X %d] [Y %d] [Z %d] \n", Dataxyz.x, Dataxyz.y, Dataxyz.z);

    __android_log_print(4, LOG_TAG,"x=[X %d],y=[Y %d],z=[Z %d]\n",Dataxyz.x, Dataxyz.y, Dataxyz.z);

    double result=fabs((double)Dataxyz.z);

    return (int)result;

    }else{

    __android_log_print(4, LOG_TAG,"open /dev/bma250 fail!");

    return -1;

    }

}
//copy GPS Data

long filesize(FILE *stream)
{
    long curpos, length;
    curpos = ftell(stream);
    fseek(stream, 0L, SEEK_END);
    length = ftell(stream);
    fseek(stream, curpos, SEEK_SET);
    return length;
}



int copyfile(const char* src,const char* dest)
{
        FILE *fp1,*fp2;
        int fsize,isdeteled,factread;
        static unsigned char buffer[SIZEOFBUFFER];
        isdeteled=remove(dest);
        if(isdeteled==0){
            __android_log_print(4, LOG_TAG, "delete sucess!");
        }
        fp1=fopen(src,"rb");
        fp2=fopen(dest,"wb+");
        if (!fp1 || !fp2) return 0;
        for (fsize=filesize(fp1);fsize>0;fsize-=SIZEOFBUFFER)
        {
        factread=fread(buffer,1,SIZEOFBUFFER,fp1);
        fwrite(buffer,factread,1,fp2);
        }
        //isdeteled=remove(src);
        //if(isdeteled==0){
            //__android_log_print(4, LOG_TAG, "delete sucess!");
        //}
        fclose(fp1);
        fclose(fp2);
        return 1;
}

static void copyFile
(JNIEnv *env, jobject obj, jstring from,jstring to)
{
    const char* c_from=(*env)->GetStringUTFChars(env,from,NULL);
    const char* c_to=(*env)->GetStringUTFChars(env,to,NULL);

    AddRootPermission();
    copyfile(c_from,c_to);
    (*env)->ReleaseStringUTFChars(env,from,c_from);
    (*env)->ReleaseStringUTFChars(env,to,c_to);
}

static jint proximityCalibration
(JNIEnv *env, jobject obj, jint value)
{

/*    int handle;
    int result = -1;
    int val = 0;

    if(value == 30){
        handle = open("/dev/cm3623_ps", O_RDONLY);
        __android_log_print(4, LOG_TAG, "proximityCalibration handle 30: %d\n", handle);
                __android_log_print(4, LOG_TAG, "error: %s\n", strerror(errno));

        result = ioctl(handle, CM3623_CALI_35, &val);
        __android_log_print(4, LOG_TAG, "proximityCalibration 35 result: %d\n", result);
    }else if(value == 0){
        handle = open("/dev/cm3623_ps", O_RDONLY);
        __android_log_print(4, LOG_TAG, "proximityCalibration handle 0: %d\n", handle);

        result = ioctl(handle, CM3623_CALI_0, &val);
        __android_log_print(4, LOG_TAG, "proximityCalibration 0 result: %d\n", result);
    }

    __android_log_print(4, LOG_TAG, "proximityCalibration result: %d\n", result);
    __android_log_print(4, LOG_TAG, "proximityCalibration val: %d, %d\n", val);

    close(handle);
    return val;*/

    int handle;
    int val = 0;
    int result = -1;

    handle = open("/dev/stk3101", O_RDONLY);
    if (handle < 0)
    {
        return -1;
    }

    if(value == 35)
    {
        result = ioctl(handle, STK_CALI_30, &val);
        __android_log_print(4, LOG_TAG, "proximityCalibration 35mm result: %d\n", result);
    }
    else if(value == 0)
    {
        result = ioctl(handle, STK_CALI_0, &val);
        __android_log_print(4, LOG_TAG, "proximityCalibration 0mm result: %d\n", result);
    }
    __android_log_print(4, LOG_TAG, "proximityCalibration val: %d\n", val);

    close(handle);
    return val;
}
//20121208 by lvhongshan start
/*
static jintArray excemotcalH
(JNIEnv * env, jobject jb)
{
#if 0
    void *handle;
        
 
        static jint nValue[] = {0,0,0,0};
        static jint reValue[] = {0,0,0,0,0};
    int (*mot_cal_user)(int[]);
    int val = 0;
        __android_log_print(4, LOG_TAG, "start exec dlopen");
    handle = dlopen("mot_cal.so",RTLD_NOW);
       
        __android_log_print(4, LOG_TAG, "handle = %0x",handle);
    if(handle == NULL){
          __android_log_print(4, LOG_TAG,"dlopen failed: %s\n", dlerror());
          return -1;
    }
         
    __android_log_print(4, LOG_TAG, "dlsym exec start");
    mot_cal_user = dlsym(handle,"mot_cal_user");
    __android_log_print(4, LOG_TAG, "mot_cal exec start");
    val = mot_cal_user(nValue);
        

   jintArray array =  (*env)->NewIntArray(env,5);
   reValue[0] = val;
   reValue[1] = nValue[0];
   reValue[2] = nValue[1];
   reValue[3] = nValue[2];
   reValue[4] = nValue[3];
    
   (*env)->SetIntArrayRegion(env,array,0,5,reValue);
    __android_log_print(4, LOG_TAG, "val = %d,x = %d,y = %d,z = %d,k = %d",val,nValue[0],nValue[1],nValue[2],nValue[3]);
    dlclose(handle);
    return array;
#endif
    static jint reValue[] = {0,0,0,0,0};
    char fastcal[3] = {0x00, 0x00, 0x02};
    jintArray array =  (*env)->NewIntArray(env,5);
    int err = -1;
    int fd;
    fd = open("/dev/bma250",O_RDWR);
    if(fd < 0)
    {
        __android_log_print(4,LOG_TAG,"open /dev/bma250 failed,fd = %d",fd);
        reValue[0] = err;
        (*env)->SetIntArrayRegion(env,array,0,5,reValue);
        return array;
    }
    err = ioctl(fd, BMA250_FAST_CALIBRATION, fastcal);
    close(fd);
    reValue[0] = err;
    (*env)->SetIntArrayRegion(env,array,0,5,reValue);
    return array;
}
*/

static jintArray excemotcalH
(JNIEnv * env, jobject jb)
{
#if 0
    void *handle;
        
 
        static jint nValue[] = {0,0,0,0};
        static jint reValue[] = {0,0,0,0,0};
    int (*mot_cal_user)(int[]);
    int val = 0;
        __android_log_print(4, LOG_TAG, "start exec dlopen");
    handle = dlopen("mot_cal.so",RTLD_NOW);
       
        __android_log_print(4, LOG_TAG, "handle = %0x",handle);
    if(handle == NULL){
          __android_log_print(4, LOG_TAG,"dlopen failed: %s\n", dlerror());
          return -1;
    }
         
    __android_log_print(4, LOG_TAG, "dlsym exec start");
    mot_cal_user = dlsym(handle,"mot_cal_user");
    __android_log_print(4, LOG_TAG, "mot_cal exec start");
    val = mot_cal_user(nValue);
        

   jintArray array =  (*env)->NewIntArray(env,5);
   reValue[0] = val;
   reValue[1] = nValue[0];
   reValue[2] = nValue[1];
   reValue[3] = nValue[2];
   reValue[4] = nValue[3];
    
   (*env)->SetIntArrayRegion(env,array,0,5,reValue);
    __android_log_print(4, LOG_TAG, "val = %d,x = %d,y = %d,z = %d,k = %d",val,nValue[0],nValue[1],nValue[2],nValue[3]);
    dlclose(handle);
    return array;
#endif
    static jint reValue[] = {0,0,0,0,0};
//20130111 modify for add BMA222 and BMA250 calibration by lvhongshan start
//    char fastcal[3] = {0x00, 0x00, 0x02};
        unsigned long value = 0 ;
//20130111 modify for add BMA222 and BMA250 calibration by lvhongshan end
    jintArray array =  (*env)->NewIntArray(env,5);
    int err = -1;
    int fd;
    //fd = open("/dev/bma222",O_RDWR);
    fd = open("/dev/bma2x2",O_RDWR);
    if(fd < 0)
    {
        __android_log_print(4,LOG_TAG,"open /dev/bma222 failed,fd = %d",fd);
//20130111 modify for add BMA222 and BMA250 calibration by lvhongshan start
                fd = open("/dev/bma250",O_RDWR);
            if(fd < 0)
            {
                __android_log_print(4,LOG_TAG,"open /dev/bma250 failed,fd = %d",fd);
                reValue[0] = err;
                (*env)->SetIntArrayRegion(env,array,0,5,reValue);
                return array;
            }
                __android_log_print(4,LOG_TAG,"open /dev/bma250 success,fd = %d",fd);
//              err = ioctl(fd, BMA250_FAST_CALIBRATION, fastcal);
            err = ioctl(fd,  BMA250_FAST_CALIBRATION, &value);
                __android_log_print(4,LOG_TAG,"after ioctl,err = %d",err);
                __android_log_print(4,LOG_TAG,"after ioctl,value = %d",value);
            close(fd);
            reValue[0] = err;
            (*env)->SetIntArrayRegion(env,array,0,5,reValue);
            return array;
//        reValue[0] = err;
//        (*env)->SetIntArrayRegion(env,array,0,5,reValue);
//        return array;
    }
        __android_log_print(4,LOG_TAG,"open /dev/bma222 success,fd = %d",fd);
//    err = ioctl(fd, BMA222_FAST_CALIBRATION, fastcal);
        err = ioctl(fd, BMA2x2_FAST_CALIBRATION, &value);
        __android_log_print(4,LOG_TAG,"after ioctl,err = %d",err);
        __android_log_print(4,LOG_TAG,"after ioctl,value = %d",value);
//20130111 modify for add BMA222 and BMA250 calibration by lvhongshan end
    close(fd);
    reValue[0] = err;
    (*env)->SetIntArrayRegion(env,array,0,5,reValue);
    return array;
}
//20121208 lvhongshanend
//2011-12-06-APP-zhangyi-Modify proximity sensor test.
static jstring getProximityPSwitch
(JNIEnv *env, jobject obj)
{
    int fd;
    int result;
    jstring ps=NULL;
    char buf[5]={0};
    fd = open("/data/simcom/cm3623_ps/ps_30_nv.file", O_RDONLY);
    __android_log_print(4, LOG_TAG, "PSwitch fd = %d", fd);

    result = read(fd, buf, sizeof(buf));
    __android_log_print(4, LOG_TAG, "PSwitch result = %d", result);
    __android_log_print(4, LOG_TAG, "PSwitch buf = %s", buf);
    __android_log_print(4, LOG_TAG, "buf len = %d", strlen(buf));
    close(fd);
    return (*env)->NewStringUTF(env,buf);
}

static jint setButtonLight(JNIEnv *env, jobject obj, jboolean b)
{
    int result;
    int value;
    if(b){
        value = 1;
    }else{
        value = 0;
    }
    result = write_int("/sys/class/leds/button-backlight/brightness", value);
    if(result < 0){
        return -1;
    }
    return 0;
}

static jstring getVersion
(JNIEnv *env, jobject obj)
{
    int fd;
    char val[40] = {0};
    struct stat buf;
    fd = open("/sys/class/i2c-dev/i2c-2/device/2-0038/ftstpfwver", O_RDONLY);

    if (fd >= 0) {
        int result = read(fd,val,40);
        close(fd);
    }
    else{
        //__android_log_print(LOG_TAG, "getVersion", "Open file failure!\n");
    }
    return (*env)->NewStringUTF(env,val);
}

static int startUpgradeTouchPannelVersion
(JNIEnv *env, jobject obj)
{
    int fd;
    char name[50] = "ft5306_truly_firmware.h";
    static int already_warned = 0;
    fd = open("/sys/class/i2c-dev/i2c-2/device/2-0038/ftsfwupgradeapp", O_WRONLY);
    if (fd >= 0) {
        int amt = write(fd, name, strlen(name));
        __android_log_print(4, LOG_TAG, "amt = %d", amt);
        close(fd);
        return(amt);
    }
    else{
        return(-1);
    }
}

static jint proximitySetCali
(JNIEnv *env, jobject obj, jint which, jint value)
{
    int fd;

    char tmp[64];
    memset(tmp, 0, 64);

    fd = open("/dev/stk3101", O_RDWR);
    if (fd >= 0)
    {
        int res = -1;
        if (which == 0)
        {

            res = ioctl(fd, STK_SET_CALI_0, &value);
        }
        else if (which == 30)
        {
            res = ioctl(fd, STK_SET_CALI_30, &value);
        }

        close(fd);
        return res;
    }
    else
    {
        return -1;
    }

}

static int startFpTest
(JNIEnv *env, jobject obj)
{
    int i,cnt;
    int fd;
    int res = -1;
    int power_en = 0;
    struct upek_info upek_info_org_tmp;
    upek_info_s *upek_info_p;

    fd = open("/dev/upek", O_RDWR);
    if (fd >= 0)
    {
        //power on
        res = ioctl(fd, UPEK_IOCTL_POWER_ON, 1);
        if (res < 0)
        {
            __android_log_print(ANDROID_LOG_VERBOSE, "---fp---", "---fp---power on fingerprint fail! \n");
            close(fd);
            return -1;
        }

        cnt = sizeof(upek_info_table)/sizeof(upek_info_s);
        upek_info_p = upek_info_table;
        for (i=0; i<cnt; i++)
        {
            upek_info_org_tmp.addr = (upek_info_p+i)->addr;
            upek_info_org_tmp.data = (upek_info_p+i)->data;
            res = ioctl(fd, UPEK_IOCTL_WRITE, &upek_info_org_tmp);
            if (res < 0)
            {
                __android_log_print(ANDROID_LOG_VERBOSE, "---fp---", "---fp---test UPEK_IOCTL_WRITE fail! \n");
                close(fd);
                return -1;
            }

            res = ioctl(fd, UPEK_IOCTL_READ, &upek_info_org_tmp);
            if (res < 0)
            {
                __android_log_print(ANDROID_LOG_VERBOSE, "---fp---", "---fp---test UPEK_IOCTL_READ fail! \n");
                close(fd);
                return -1;
            }
            switch((upek_info_p+i)->tri_state)
            {
                case 0:    //all
                break;

                case 1:    //low
                upek_info_org_tmp.data &= 0xF;
                break;

                case 2:    //high
                upek_info_org_tmp.data &= 0xF0;
                break;
            }

            if (upek_info_org_tmp.data != (upek_info_p+i)->succ)
            {
                __android_log_print(ANDROID_LOG_VERBOSE, "---fp---", "---fp---error value! \n");
                close(fd);
                return -1;
            }

        }

        //power off
        res = ioctl(fd, UPEK_IOCTL_POWER_DOWN, 1);
        if (res < 0)
        {
            __android_log_print(ANDROID_LOG_VERBOSE, "---fp---", "---fp---power off fingerprint fail! \n");
            close(fd);
            return -1;
        }

        close(fd);
        return res;
    }
    else
    {
        __android_log_print(ANDROID_LOG_VERBOSE, "---fp---", "---fp---open fingerprint fail! \n");
        return -1;
    }
}
//20130105 add for test Lkt by lvhongshan start
/* The file descriptor for a serial device.  */
static int serial_fd = -1;

static int lkt_fd = -1;

/* The file name of a serial device.  */
static char *serial_device = NULL;

unsigned char buffer_r[128];
unsigned char buffer_w[128];

/* Set the file name of a serial device (or a pty device). This is a
   function specific to the grub shell.  */
void
LTK_serial_set_device (const char *device)
{
  if (serial_device)
    free (serial_device);

  serial_device = strdup (device);
}
void
LTK_serial_free_device (void)
{
  if (serial_device)
    free (serial_device);
}

int  LTK_Hex2Byte(char *buf, size_t len,unsigned char *dat0)
{
    int i,j,k,leth;
    char h,l;

    leth=len;
    printf("LTK_Hex2Byte =%d ",len);
    leth>>=1;
    printf("LTK_Hex2Byte leth=%d\n",leth);
    for(k=0;k<leth;k++)
    {
        h=buf[2*k];
        l=buf[2*k + 1];
        i=16;
        j=16;
        if ((h<='9') && (h>='0'))
            i=(h-'0') ;
        if ((h<='F') && (h>='A'))
            i=(h-'A'+10);

        if ((l<='9') && (l>='0'))
            j=(l-'0') ;
        if ((l<='F') && (l>='A'))
            j=(l-'A'+10);

        if  (i+j>=32)
        {
            //dat0[k]=(byte)str0.GetAt(2*k);
            return 0;
        }else
        {
            dat0[k]=(i<<4)+j;
        }
    }
    return (leth);
}

static speed_t
LTK_get_termios_speed (int speed)
{
    switch (speed)
    {
    case 2400: return B2400;
    case 4800: return B4800;
    case 9600: return B9600;
    case 19200: return B19200;
    case 38400: return B38400;
#ifdef B57600
    case 57600: return B57600;
#endif
#ifdef B115200
    case 115200: return B115200;
#endif
    }
    return B0;
}

int
LTK_serial_hw_init (unsigned int speed,int word_len, int parity, int stop_bit_len)
{
  struct termios termios;
  speed_t termios_speed;
  int i;

  printf("LTK_serial_hw_init %s,speed %d\n",serial_device,speed);
  /* Check if the file name is specified.  */
  if (! serial_device)
    return 0;

  /* If a serial device is already opened, close it first.  */
  if (serial_fd >= 0)
    close (serial_fd);

  /* Open the device file.  */
  serial_fd = open (serial_device,
            O_RDWR | O_NOCTTY
#if defined(O_SYNC)
            /* O_SYNC is used in Linux (and some others?).  */
            | O_SYNC
#elif defined(O_FSYNC)
            /* O_FSYNC is used in FreeBSD.  */
            | O_FSYNC
#endif
            );

  if (serial_fd < 0)
    return 0;

  printf("LTK_serial_hw_init open ok\n");

  /* Get the termios parameters.  */
  if (tcgetattr (serial_fd, &termios))
    goto fail;

  /* Raw mode.  */
  cfmakeraw (&termios);

  /* Set the speed.  */
  termios_speed = LTK_get_termios_speed (speed);
  if (termios_speed == B0)
    goto fail;

  cfsetispeed (&termios, termios_speed);
  cfsetospeed (&termios, termios_speed);

  /* Set the word length.  */
  termios.c_cflag &= ~CSIZE;
  switch (word_len)
    {
    case UART_5BITS_WORD:
      termios.c_cflag |= CS5;
      break;
    case UART_6BITS_WORD:
      termios.c_cflag |= CS6;
      break;
    case UART_7BITS_WORD:
      termios.c_cflag |= CS7;
      break;
    case UART_8BITS_WORD:
      termios.c_cflag |= CS8;
      break;
    default:
      goto fail;
    }

  /* Set the parity.  */
  switch (parity)
    {
    case UART_NO_PARITY:
      termios.c_cflag &= ~PARENB;
      break;
    case UART_ODD_PARITY:
      termios.c_cflag |= PARENB;
      termios.c_cflag |= PARODD;
      break;
    case UART_EVEN_PARITY:
      termios.c_cflag |= PARENB;
      termios.c_cflag &= ~PARODD;
      break;
    default:
      goto fail;
    }

  /* Set the length of stop bit.  */
  switch (stop_bit_len)
    {
    case UART_1_STOP_BIT:
      termios.c_cflag &= ~CSTOPB;
      break;
    case UART_2_STOP_BITS:
      termios.c_cflag |= CSTOPB;
      break;
    default:
      goto fail;
    }

  /* Set the parameters.  */
  if (tcsetattr (serial_fd, TCSANOW, &termios))
    goto fail;

  return 1;

 fail:
    printf("LTK_serial_hw_init fail\n");
  close (serial_fd);
  serial_fd = -1;
  return 0;
}

static void* thread_read(void* arg)
{
    int count    =50,len,i;
    printf("++++++++++++++++\n");
    while (1)
    {
            //memset(buffer_r,'\0',sizeof(buffer_r));
        len = read(serial_fd, buffer_r, sizeof(buffer_r));
        printf("read len = %d\n",len);
                __android_log_print(4, LOG_TAG, "length: %d \n",len);
        for(i=0;i<len;i++)
        {
            printf("%X ",buffer_r[i]);
            printf("%d ",buffer_r[i]);
            printf("%c \n",buffer_r[i]);
                         __android_log_print(4, LOG_TAG, "[%d] %X <--> %d",i ,buffer_r[i] ,buffer_r[i]);
        }
        printf("\n");
    }
    return NULL;
}

static jint lktTest
(JNIEnv *env, jobject obj)//(int argc, char **argv)
{
    char port[20];
    int result = 0;
    int tresult=0;
    int len = 0;
    int writelen=0;
    pthread_t thread;
    unsigned int loop_count=0;
    unsigned char pwr_status = 1;

    LTK_serial_set_device("/dev/ttyMSM2");
    if (!LTK_serial_hw_init (19200,UART_8BITS_WORD,UART_EVEN_PARITY,UART_2_STOP_BITS))
    {
        LTK_serial_free_device();
                printf("LTK_serial_hw_init fail\n");
        return 1;
    }

        lkt_fd = open("/dev/lkt",O_RDWR);
        printf("open lkt %d\n",lkt_fd);
        if(lkt_fd < 0)
        {
            close(lkt_fd);
            return 1;
        }

        pthread_create(&thread, NULL, thread_read, NULL);
        memset(buffer_r,'\0',sizeof(buffer_r));
    sleep(1);//sleep(5);

        // reset lkt chip
        result = ioctl(lkt_fd, MSM_LKT_RESET,0);
        if(!result)
        {
            printf("reset lkt OK\n");
        }
        else
        {
            close(lkt_fd);
            return -1;
        }
        sleep(1);

        //for(;loop_count<1000;loop_count++){
            //printf("loop %d\n",loop_count);
        // send test package to LKT chip
        len = LTK_Hex2Byte("F0F6000108",sizeof("F0F6000108"),&buffer_w[0]);
       //len = LTK_Hex2Byte("A306011122334455",sizeof("A306011122334455"),&buffer_w[0]);
    printf("write len %d\n",len);
    printf("%X-%X-%X-%X-%X-%X-%X-%X ",
        buffer_w[0],buffer_w[1],buffer_w[2],buffer_w[3]
        ,buffer_w[4],buffer_w[5],buffer_w[6],buffer_w[7]);
        __android_log_print(4, LOG_TAG, "start test ");
    writelen=write(serial_fd,buffer_w,len);
    printf("writelen len =%d\n",writelen);
        

        memset(buffer_r,0xFF,sizeof(buffer_r));
        memset(buffer_w,0xFF,sizeof(buffer_w));
    sleep(1); //sleep(10);
         __android_log_print(4, LOG_TAG, "1: %X-%X-%X-%X-%X-%X-%X-%X-%X-%X-%X ",
        buffer_r[0],buffer_r[1],buffer_r[2],buffer_r[3]
        ,buffer_r[4],buffer_r[5],buffer_r[6],buffer_r[7],buffer_r[8],buffer_r[9],buffer_r[10]);
    if(buffer_r[0]== 246 && buffer_r[9] == 144 && buffer_r[10] == 0){
        //F6 6F 10 10 A1 53 38 AB 26 90 0
        
        tresult++;
    }
    __android_log_print(4, LOG_TAG, "reset1");
    memset(buffer_r,'\0',sizeof(buffer_r));
            //}  // for(;loop_count<1000;loop_count++)
//20130105 modify for remove A306011122334455 by lvhongshan start
//       len = LTK_Hex2Byte("A306011122334455",sizeof("A306011122334455"),&buffer_w[0]);
//    printf("write len %d\n",len);
//    printf("%X-%X-%X-%X-%X-%X-%X-%X ",
//        buffer_w[0],buffer_w[1],buffer_w[2],buffer_w[3]
//        ,buffer_w[4],buffer_w[5],buffer_w[6],buffer_w[7]);
//
//    writelen=write(serial_fd,buffer_w,len);
//    printf("writelen len =%d\n",writelen);
//        sleep(5);
//20130105 modify for remove A306011122334455 by lvhongshan end
       //return result;

/*===========================================================*/
        // must reset LKT,before sending FF10947B
        result = ioctl(lkt_fd, MSM_LKT_RESET,0);
        if(!result)
        {
            printf("reset lkt OK 222\n");
        }
        else
        {
            close(lkt_fd);
            return -1;
        }
        sleep(1);

        // set PPS of LKT chip
        len = LTK_Hex2Byte("FF10947B",sizeof("FF10947B"),&buffer_w[0]);   // set PPS to 115200 bps
        printf("write len %d\n",len);
    printf("%X-%X-%X-%X-%X-%X-%X-%X ",
        buffer_w[0],buffer_w[1],buffer_w[2],buffer_w[3]
        ,buffer_w[4],buffer_w[5],buffer_w[6],buffer_w[7]);

    writelen=write(serial_fd,buffer_w,len);
    printf("writelen len =%d\n",writelen);

 //       memset(buffer_r,0,sizeof(buffer_r));
//        memset(buffer_w,0,sizeof(buffer_w));

    sleep(3); //sleep(10);
        __android_log_print(4, LOG_TAG, "2: %X-%X-%X-%X-%X-%X-%X-%X ",
        buffer_r[0],buffer_r[1],buffer_r[2],buffer_r[3]
        ,buffer_r[4],buffer_r[5],buffer_r[6],buffer_r[7]);
    if(buffer_r[0]== 255 && buffer_r[1] == 16 && buffer_r[2] == 148 && buffer_r[3] == 123){
            //FF10947B
                
            tresult++;
        }
         __android_log_print(4, LOG_TAG, "reset2");
         memset(buffer_r,'\0',sizeof(buffer_r));
/*===========================================================*/
    if (!LTK_serial_hw_init (115200,UART_8BITS_WORD,UART_EVEN_PARITY,UART_2_STOP_BITS))
    {
        LTK_serial_free_device();
                printf("LTK_serial_hw_init fail\n");
        return -1;
    }

//for(;loop_count<1000;loop_count++){
//        printf("loop %d\n",loop_count);
        len = LTK_Hex2Byte("F0F6000108",sizeof("F0F6000108"),&buffer_w[0]);
       //len = LTK_Hex2Byte("A306011122334455",sizeof("A306011122334455"),&buffer_w[0]);
    printf("write len %d\n",len);
    printf("%X-%X-%X-%X-%X-%X-%X-%X ",
        buffer_w[0],buffer_w[1],buffer_w[2],buffer_w[3]
        ,buffer_w[4],buffer_w[5],buffer_w[6],buffer_w[7]);

    writelen=write(serial_fd,buffer_w,len);
    printf("writelen len =%d\n",writelen);

        //memset(buffer_r,0,sizeof(buffer_r));
        //memset(buffer_w,0,sizeof(buffer_w));
        sleep(1); //sleep(10);
        __android_log_print(4, LOG_TAG, "3: %X-%X-%X-%X-%X-%X-%X-%X-%X-%X-%X ",
        buffer_r[0],buffer_r[1],buffer_r[2],buffer_r[3]
        ,buffer_r[4],buffer_r[5],buffer_r[6],buffer_r[7],buffer_r[8],buffer_r[9],buffer_r[10]);
    if(buffer_r[0]== 246 && buffer_r[9] == 144 && buffer_r[10] == 0){
            //F6 6F 10 10 A1 53 38 AB 26 90 0

            tresult++;
        }
         __android_log_print(4, LOG_TAG, "reset3");
         memset(buffer_r,'\0',sizeof(buffer_r));
//20130105 modify for remove A306011122334455 by lvhongshan start
//       len = LTK_Hex2Byte("A306011122334455",sizeof("A306011122334455"),&buffer_w[0]);
//    printf("write len %d\n",len);
//    printf("%X-%X-%X-%X-%X-%X-%X-%X ",
//        buffer_w[0],buffer_w[1],buffer_w[2],buffer_w[3]
//        ,buffer_w[4],buffer_w[5],buffer_w[6],buffer_w[7]);
//
//    writelen=write(serial_fd,buffer_w,len);
//    printf("writelen len =%d\n",writelen);
//
//        //memset(buffer_r,0,sizeof(buffer_r));
//        //memset(buffer_w,0,sizeof(buffer_w));
//        sleep(1); //sleep(10);
//20130105 modify for remove A306011122334455 by lvhongshan end
// }

    close(lkt_fd);
    close (serial_fd);
    __android_log_print(4, LOG_TAG, "end test result: %d ", tresult);
    return tresult;
}

//20130105 add for test Lkt by lvhongshan end

static jstring getHWVersion
(JNIEnv *env, jobject obj)
{
    AddRootPermission();
    int handle = open("/sys/devices/soc0/platform_version", O_RDONLY);
    char val[20]={0};
    int iret = 0;
    int test =55;
    if(handle == -1)
    {
        __android_log_print(4, LOG_TAG, "open the cal_ps_data failed");
    }else
    {
        iret = read(handle,val,20);
        //val[9] = '\0';
        __android_log_print(4, LOG_TAG, "iret = %d,val = %s",iret,val);
    }
    close(handle);
        return (*env)->NewStringUTF(env,val);
}

//Modify for CIT optimization by xiasiping 20140730 start
static jstring getHWVersion_d
(JNIEnv *env, jobject obj, jstring str)
{
    AddRootPermission();

    const char *vres = (*env)->GetStringUTFChars(env,str,0);
   // int handle = open("/sys/devices/soc0/platform_version", O_RDONLY);
    int handle = open(vres, O_RDONLY);
    char val[20]={0};
    int iret = 0;
    int test =55;
    if(handle == -1)
    {
        __android_log_print(4, LOG_TAG, "open the cal_ps_data failed");
    }else
    {
        iret = read(handle,val,20);
        //val[9] = '\0';
        __android_log_print(4, LOG_TAG, "iret = %d,val = %s",iret,val);
    }
    close(handle);
        return (*env)->NewStringUTF(env,val);
}
//Modify for CIT optimization by xiasiping 20140730 end

static jstring getHWSubType
(JNIEnv *env, jobject obj)
{
    AddRootPermission();
    int handle = open("/sys/devices/soc0/platform_subtype", O_RDONLY);
    char val[20]={0};
    int iret = 0;
    int test =55;
    if(handle == -1)
    {
        __android_log_print(4, LOG_TAG, "open the cal_ps_data failed");
    }else
    {
        iret = read(handle,val,20);
        //val[19] = '\0';
        __android_log_print(4, LOG_TAG, "iret = %d,val = %s",iret,val);
    }
    close(handle);
        return (*env)->NewStringUTF(env,val);
}

//Modify for CIT optimization by xiasiping 20140730 start
static jstring getHWSubType_d
(JNIEnv *env, jobject obj, jstring str)
{
    AddRootPermission();
    //int handle = open("/sys/devices/soc0/platform_subtype", O_RDONLY);
    const char *vres = (*env)->GetStringUTFChars(env,str,0);
    int handle = open(vres, O_RDONLY);
    char val[20]={0};
    int iret = 0;
    int test =55;
    if(handle == -1)
    {
        __android_log_print(4, LOG_TAG, "open the cal_ps_data failed");
    }else
    {
        iret = read(handle,val,20);
        //val[19] = '\0';
        __android_log_print(4, LOG_TAG, "iret = %d,val = %s",iret,val);
    }
    close(handle);
        return (*env)->NewStringUTF(env,val);
}
//Modify for CIT optimization by xiasiping 20140730 end
//Modify for P-sensor change in PA568 by xiasiping 20140919 start
static jint getXPsensor
(JNIEnv *env, jobject obj)
{
    char value_path[50] = "/sys/class/input/input5/ps_citvalue";
    int k;
    int fd_value;
    int status;
    int value;
    char buf[10] = {0};

    fd_value = open (value_path,O_RDONLY);
        if (fd_value < 0)
        {
            __android_log_print(4, LOG_TAG, "open the psensor_value failed");
            return -1;
        }
        status = system ("/system/bin/psensor_raw_value");
        if (status < 0)
        {
            __android_log_print(4, LOG_TAG, "open system_psensor_raw_value failed");
            return -2;
        }
        status = read (fd_value,buf,10);
        if (status < 0)
        {
            __android_log_print(4, LOG_TAG, "get psensor_value failed");
            return -3;
        }
    value = atoi (buf);
    printf ("value = %d\n",value);
    close (fd_value);
    return value;
}



static jint getXPsensor_new
(JNIEnv *env, jobject obj)
{
    //Modify for change path by lizhaobo 20150813 start
    char value_path[50] = "/sys/devices/soc.0/78b6000.i2c/i2c-0/0-0053/ps_data";
    //Modify for change path by lizhaobo 20150813 end
    char value_path1[50] = "/sys/class/input/input2/ps_citvalue";
    char value_path2[50] = "/sys/class/input/input3/ps_citvalue";
    char value_path3[50] = "/sys/class/input/input4/ps_citvalue";
    char value_path4[50] = "/sys/class/input/input5/ps_citvalue";
    int k;
    int fd_value;
    int status;
    int value;
    char buf[10] = {0};

    fd_value = open (value_path,O_RDONLY);
        if (fd_value < 0)
        {
            __android_log_print(4, LOG_TAG, "open the psensor_value failed");
            fd_value = open (value_path1,O_RDONLY);
            if (fd_value < 0)
            {
                __android_log_print(4, LOG_TAG, "open the psensor_value1 failed");
                fd_value = open (value_path2,O_RDONLY);
                if (fd_value < 0)
                {
                    __android_log_print(4, LOG_TAG, "open the psensor_value2 failed");
                    fd_value = open (value_path3,O_RDONLY);
                    if (fd_value < 0)
                    {
                        __android_log_print(4, LOG_TAG, "open the psensor_value3 failed");
                        fd_value = open (value_path4,O_RDONLY);
                        if (fd_value < 0)
                        {
                            __android_log_print(4, LOG_TAG, "open the psensor_value4 failed");
                            return -1;
                        }
                    }
                }
            }
        }
        /*status = system ("/system/bin/psensor_raw_value");
        if (status < 0)
        {
            __android_log_print(4, LOG_TAG, "open system_psensor_raw_value failed");
            return -2;
        }*/
        status = read (fd_value,buf,10);
        if (status < 0)
        {
            __android_log_print(4, LOG_TAG, "get psensor_value failed");
            return -3;
        }
    value = atoi (buf);
    printf ("value = %d\n",value);
    close (fd_value);
    return value;
}

static jint setPsensorNear
(JNIEnv *env, jobject obj, jstring str)
{
    char near_path[50] = "/data/psensor/psensor_near";
    const char *vstr = (*env)->GetStringUTFChars(env,str,0);
    int fd_near;
    int status;

    fd_near = open (near_path,O_RDWR);
    if (fd_near < 0)
    {
        __android_log_print(4, LOG_TAG, "open the psensor_near failed");
        return -4;
    }
    status = write (fd_near, vstr, 10);
    if (status < 0)
    {
        __android_log_print(4, LOG_TAG, "set the psensor_near failed");
        return -5;
    }
    close (fd_near);
    return 0;
}

static jint setPsensorFar
(JNIEnv *env, jobject obj, jstring str)
{
    //Modify for change path by lizhaobo 20150813 start
    char far_path[50] = "/sys/bus/i2c/devices/0-0039/ps_5_cal_value";
    //Modify for change path by lizhaobo 20150813 end
    const char *vstr = (*env)->GetStringUTFChars(env,str,0);
    int fd_far;
    int status;

    fd_far = open (far_path, O_RDWR);
    if (fd_far < 0)
    {
        __android_log_print(4, LOG_TAG, "open the psensor_far failed");
        return -4;
    }
    status = write (fd_far, vstr, 10);
    if (status < 0)
    {
        __android_log_print(4, LOG_TAG, "set the psensor_far failed");
        return -5;
    }
    close (fd_far);
    return 0;
}
//Modify for P-sensor change in PA568 by xiasiping 20140919 end

static jint GyroCalibration
(JNIEnv *env, jobject obj)
{
    int fd;
    char buf[10] ={0};
  //  system ("cat /sys/class/sensors/MPU6050-gyro/self_test");
    fd = open("/sys/class/sensors/MPU6050-gyro/self_test", O_RDONLY);
    if (fd < 0) {
        printf("open file failed!!/n");
        __android_log_print(4, LOG_TAG, "GyroCalibration open failed!!");
        fd = open ("sys/class/sensors/bmi160-gyro/self_test", O_RDONLY);
        if (fd < 0)
        {
            __android_log_print(4, LOG_TAG, "GyroCalibration open failed!!");
            close(fd);
            return -1;

        }
    }
    read (fd,buf,10);
    sleep(1);
    close(fd);
    return 0;
}


static jint getGyro_x
(JNIEnv *env, jobject obj)
{
    //Modify for change path by lizhaobo 20150813 start
    char off_gx[50] = "/sys/bus/i2c/devices/0-0068/gyro_offset_x";
    //Modify for change path by lizhaobo 20150813 end
    int fd_x;
    int value_x=0;
    char buf_x[10] = {0};
    fd_x = open (off_gx,O_RDONLY);
    read (fd_x,buf_x,10);
    value_x = atoi(buf_x);
    close(fd_x);
    return value_x;
}

static jint getGyro_y
(JNIEnv *env, jobject obj)
{
    //Modify for change path by lizhaobo 20150813 start
    char off_gy[50] = "/sys/bus/i2c/devices/0-0068/gyro_offset_y";
    //Modify for change path by lizhaobo 20150813 end
    int fd_y;
    int value_y=0;
    char buf_y[10] = {0};
    fd_y = open (off_gy,O_RDONLY);
    read (fd_y,buf_y,10);
    value_y = atoi(buf_y);
    close(fd_y);
    return value_y;
}

static jint getGyro_z
(JNIEnv *env, jobject obj)
{
    //Modify for change path by lizhaobo 20150813 start
    char off_gz[50] = "/sys/bus/i2c/devices/0-0068/gyro_offset_z";
    //Modify for change path by lizhaobo 20150813 end
    int fd_z;
    int value_z=0;
    char buf_z[10] = {0};
    fd_z = open (off_gz,O_RDONLY);
    read (fd_z,buf_z,10);
    value_z = atoi(buf_z);
    close(fd_z);
    return value_z;
}


static jstring GsensorCalibration
(JNIEnv *env, jobject obj)
{
    char buff[5]   = {0};
    char path[50]  = "/sys/class/sensors/MPU6050-accel/self_test";
    char path_1[50]  = "/sys/class/sensors/bmi160-accel/self_test";
    //Modify for change path by lizhaobo 20150813 start
    char off_x[50] = "/sys/bus/i2c/devices/0-0068/acc_offset_x";
    char off_y[50] = "/sys/bus/i2c/devices/0-0068/acc_offset_y";
    char off_z[50] = "/sys/bus/i2c/devices/0-0068/acc_offset_z";
    //Modify for change path by lizhaobo 20150813 end
    int fd;
    int fd_x,fd_y,fd_z;
    char buf_x[10] = {0};
    char buf_y[10] = {0};
    char buf_z[10] = {0};
    char buf_xyz[30] = {0};

    int ret = 0;
    fd_x = open (off_x,O_RDWR);
    fd_y = open (off_y,O_RDWR);
    fd_z = open (off_z,O_RDWR);
    AddRootPermission();
    fd = open(path, O_RDONLY);
    if (fd < 0) {
        __android_log_print(4, LOG_TAG, "GyroCalibration open failed!!");
        fd = open (path_1, O_RDONLY);
        if (fd < 0)
        {
            printf("G-sensor-calibration failed!\n");
            __android_log_print(4, LOG_TAG, "GsensorCalibration open failed");
            strcpy (buf_xyz,"fail");
            return (*env)->NewStringUTF(env,buf_xyz);

        }
    }
    int size = read(fd, buff, 5);
        __android_log_print(4, LOG_TAG, "size = %d",size);
        __android_log_print(4, LOG_TAG, "buff = %s",buff);
    if (size < 0) {
        close(fd);
        strcpy (buf_xyz,"fail");
        return (*env)->NewStringUTF(env,buf_xyz);
    }
    if (strncmp(buff, "pass", 4) == 0) {
        close(fd);
    } 
    else {
        close(fd);
    }
    usleep(100000);
    ret = read (fd_x,buf_x,10);
    if (ret < 0)
    {
        strcpy (buf_xyz,"fail");
    //return (*env)->NewStringUTF(env,buf_xyz);
    }
    read (fd_y,buf_y,10);
    read (fd_z,buf_z,10);
    close (fd_x);
    close (fd_y);
    close (fd_z);
    strcpy (buf_xyz,buf_x);
    strcat (buf_xyz,",");
    strcat (buf_xyz,buf_y);
    strcat (buf_xyz,",");
    strcat (buf_xyz,buf_z);
    __android_log_print(4, LOG_TAG, "buf_xyz = %s", buf_xyz);
    return (*env)->NewStringUTF(env,buf_xyz);
}

//Add for xpand urt test in PA568 by lvhongshan 20140929 start
static jstring urtTtyTestOne
(JNIEnv *env, jobject obj, jstring str)
{
    int flags = 0;
    AddRootPermission();
    __android_log_print(4, LOG_TAG, "urtTtyTestOne() start");
    int handle = open("/dev/ttyHSL2", O_RDWR);
    flags = fcntl(handle, F_GETFL, 0);
    fcntl(handle, F_SETFL, flags | O_NONBLOCK);
    __android_log_print(4, LOG_TAG, "opened the /dev/ttyHSL2");
    char val[4]={0};
    const char *vstr = (*env)->GetStringUTFChars(env,str,0);
    __android_log_print(4, LOG_TAG, "GetStringUTFChars %s",vstr);
    int iret = 0;
    int writestatus = 0;
    if(handle == -1)
    {
        __android_log_print(4, LOG_TAG, "open the /dev/ttyHSL2 failed");
    }else
    {
        serial_set_speed(handle, 9600);
        iret = serial_set_parity(handle, 8, 'n', 1);
        __android_log_print(4, LOG_TAG, "before write %d ",iret);
        //while(1){
        writestatus = write (handle, vstr, 4);
        //}
        sleep(1);
        __android_log_print(4, LOG_TAG, "before read %d ",iret);
        iret = read(handle,val,4);
        //val[9] = '\0';
        __android_log_print(4, LOG_TAG, "writestatus = %d,iret = %d,val = %s",writestatus,iret,val);
    }
    close(handle);
    return (*env)->NewStringUTF(env,val);
}

static jstring urtTtyTestTwo
(JNIEnv *env, jobject obj, jstring str)
{
    int flags = 0;
    AddRootPermission();
    __android_log_print(4, LOG_TAG, "urtTtyTestTwo() start");
    int handle = open("/dev/ttyHSL4", O_RDWR);
    flags = fcntl(handle, F_GETFL, 0);
    fcntl(handle, F_SETFL, flags | O_NONBLOCK);
    char val[4]={0};
    const char *vstr = (*env)->GetStringUTFChars(env,str,0);
    __android_log_print(4, LOG_TAG, "GetStringUTFChars %s",vstr);
    int iret = 0;
    int writestatus = 0;
    if(handle == -1)
    {
        __android_log_print(4, LOG_TAG, "open the /dev/ttyHSL4 failed");
    }else
    {
        serial_set_speed(handle, 9600);
        iret = serial_set_parity(handle, 8, 'n', 1);
        __android_log_print(4, LOG_TAG, "before write %d ",iret);
        writestatus = write (handle, vstr, 4);
        sleep(1);
        __android_log_print(4, LOG_TAG, "before read %d ",iret);

        iret = read(handle,val,4);
        //val[9] = '\0';
        __android_log_print(4, LOG_TAG, "writestatus = %d,iret = %d,val = %s",writestatus,iret,val);
    }
    close(handle);
    return (*env)->NewStringUTF(env,val);
}
//Add for xpand urt test in PA568 by lvhongshan 20140929 end

static const char *classPathName = "com/sim/cit/CommonDrive";

static JNINativeMethod methods[] = {
//      {"flashlightControl", "(Ljava/lang/String;)V", (void*)flashlightControl },
    {"backlightControl", "(I)V", (void*)backlightControl },
    {"lightControl", "(I)V", (void*)lightControl },
        //Modify for CIT optimization by xiasiping 20140730 start
    {"lightControl_d", "(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", (void*)lightControl_d },
        //Modify for CIT optimization by xiasiping 20140730 end
    {"calibration", "()I", (void*)calibration },
    {"getMotionXYZ", "()[F", (void*)getMotionXYZ },
    {"getCompassValues", "()[I", (void*)getCompassValues },
      {"getOrientationValues", "()[I", (void*)getOrientationValues },
      {"proximityDistance", "()Ljava/lang/String;", (void*)proximityDistance },
      {"lightDegree", "()Ljava/lang/String;", (void*)lightDegree },
    {"getVoltage", "()I", (void*)getVoltage },
        //Modify for CIT optimization by xiasiping 20140730 start
    {"getVoltage_d", "(Ljava/lang/String;)I", (void*)getVoltage_d },
        //Modify for CIT optimization by xiasiping 20140730 end
      {"getCapacity", "()I", (void*)getCapacity },
      {"getHardwareVersion", "()I", (void*)getHardwareVersion },
    {"wifiControl", "(II)I", (void*)wifiControl },
      {"bluetoothControl", "(Z)I", (void*)bluetoothControl },
      {"openBluetooth", "()I", (void*)openBluetooth },
      {"excemotcal", "()[I", (void*)excemotcal },
    {"execVcalibration", "()I", (void*)execVcalibration },
      {"copyFile", "(Ljava/lang/String;Ljava/lang/String;)V", (void*)copyFile },
    {"proximityCalibration", "(I)I", (void*)proximityCalibration },
    {"excemotcalH", "()[I", (void*)excemotcalH },
    /*
    {"startWifiTestMode", "()I", (void*)startWifiTestMode },
      {"stopWifiTestMode", "()I", (void*)stopWifiTestMode },
    {"InitWifiTestMode", "()I", (void*)InitWifiTestMode },
      {"deinitWifiTestMode", "()I", (void*)deinitWifiTestMode },
    {"setWifiMode", "(II)I", (void*)setWifiMode },
    {"wifiStartRx", "(I)I", (void*)wifiStartRx },
    {"setRxEquipmentType", "(I)I", (void*)setRxEquipmentType },
      {"setRxTotalPacket", "(I)I", (void*)setRxTotalPacket },
    {"wifiStopRx", "()I", (void*)wifiStopRx },
      {"readwifiRxResult", "()[I", (void*)readwifiRxResult },
    {"readWifiRxRssi", "()I", (void*)readWifiRxRssi },
    {"wifiStartTx", "(I)I", (void*)wifiStartTx },
    {"wifiStopTx", "()I", (void*)wifiStopTx },
    {"wifiSetTxGain", "(I)I", (void*)wifiSetTxGain },
    {"wifiSetTxSIFS", "(I)I", (void*)wifiSetTxSIFS },
      {"wifiSetTxNumberofFrames", "(I)I", (void*)wifiSetTxNumberofFrames },
    {"wifiSetPayLoadLength", "(I)I", (void*)wifiSetPayLoadLength },
      {"wifiSetPreamble", "(I)I", (void*)wifiSetPreamble },
    {"readwifiRxStatus", "()[I", (void*)readwifiRxStatus },
    {"wifiClearRxStatus", "()I", (void*)wifiClearRxStatus },
    {"getWifiMacAddress", "()Ljava/lang/String;", (void*)getWifiMacAddress },
    {"setWifiMacAddress", "(Ljava/lang/String;)I", (void*)setWifiMacAddress },
    {"setCarrierSuppressionMode", "(I)V", (void*)setCarrierSuppressionMode },
      {"runCommand", "(Ljava/lang/String;)I", (void*)runCommand },
      */
      {"setButtonLight", "(Z)I", (void*)setButtonLight },
      {"getProximityPSwitch", "()Ljava/lang/String;", (void*)getProximityPSwitch },
      {"getVersion", "()Ljava/lang/String;", (void*)getVersion },
      {"startUpgradeTouchPannelVersion", "()I", (void*)startUpgradeTouchPannelVersion },
    {"proximitySetCali", "(II)I", (void*)proximitySetCali },
        {"getHWVersion", "()Ljava/lang/String;", (void*)getHWVersion },
        //Modify for CIT optimization by xiasiping 20140730 start
        {"getHWVersion_d", "(Ljava/lang/String;)Ljava/lang/String;", (void*)getHWVersion_d },
        //Modify for CIT optimization by xiasiping 20140730 end
    {"getHWSubType", "()Ljava/lang/String;", (void*)getHWSubType },
        //Modify for CIT optimization by xiasiping 20140730 start
    {"getHWSubType_d", "(Ljava/lang/String;)Ljava/lang/String;", (void*)getHWSubType_d },
        //Modify for CIT optimization by xiasiping 20140730 end
    {"buttonlightControl", "(I)V", (void*)buttonlightControl },
    //Modify for CIT optimization by xiasiping 20140730 start
    {"buttonlightControl_d", "(ILjava/lang/String;)V", (void*)buttonlightControl_d },
    //Modify for CIT optimization by xiasiping 20140730 end
    {"getTemp", "()I", (void*)getTemp },
    //Modify for CIT optimization by xiasiping 20140730 start
    {"getTemp_d", "(Ljava/lang/String;)I", (void*)getTemp_d },
    //Modify for CIT optimization by xiasiping 20140730 end
    {"lightControlForIS", "(I)V", (void*)lightControlForIS},
        //modify for add heartbeatforPCBA by xiasiping 20140925 start
    {"PtestHeartBeat", "()I", (void*)PtestHeartBeat },
    {"CtestHeartBeat", "()I", (void*)CtestHeartBeat },
        //modify for add heartbeatforPCBA by xiasiping 20140925 end
        //Modify for P-sensor change in PA568 by xiasiping 20140919 start
    {"getXPsensor", "()I", (void*)getXPsensor },
    {"getXPsensor_new", "()I", (void*)getXPsensor_new },
    {"setPsensorNear", "(Ljava/lang/String;)I", (void*)setPsensorNear },
    {"setPsensorFar", "(Ljava/lang/String;)I", (void*)setPsensorFar },
        //Modify for P-sensor change in PA568 by xiasiping 20140919 end
    {"urtTtyTestOne", "(Ljava/lang/String;)Ljava/lang/String;", (void*)urtTtyTestOne },
    {"GsensorCalibration", "()Ljava/lang/String;", (void*)GsensorCalibration },
    {"GyroCalibration", "()I", (void*)GyroCalibration },
    {"getGyro_x", "()I", (void*)getGyro_x },
    {"getGyro_y", "()I", (void*)getGyro_y },
    {"getGyro_z", "()I", (void*)getGyro_z },
    {"urtTtyTestTwo", "(Ljava/lang/String;)Ljava/lang/String;", (void*)urtTtyTestTwo }

};

static int registerNativeMethods(JNIEnv* env, const char* className,
    JNINativeMethod* gMethods, int numMethods)
{
    jclass clazz;

    clazz = (*env)->FindClass(env, className);
    if (clazz == NULL) {
        __android_log_print(4, LOG_TAG, "Native registration unable to find class '%s'", className);
        return JNI_FALSE;
    }
    if ((*env)->RegisterNatives(env, clazz, gMethods, numMethods) < 0) {
        __android_log_print(4, LOG_TAG, "RegisterNatives failed for '%s'", className);
        return JNI_FALSE;
    }

    return JNI_TRUE;
}

static int registerNatives(JNIEnv* env)
{
  if (!registerNativeMethods(env, classPathName,
                 methods, sizeof(methods) / sizeof(methods[0]))) {
    return JNI_FALSE;
  }

  return JNI_TRUE;
}
 
typedef union {
    JNIEnv* env;
    void* venv;
} UnionJNIEnvToVoid;

jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
    UnionJNIEnvToVoid uenv;
    uenv.venv = NULL;
    jint result = -1;
    JNIEnv* env = NULL;
    
    if ((*vm)->GetEnv(vm, &uenv.venv, JNI_VERSION_1_4) != JNI_OK) {
        __android_log_print(4, LOG_TAG, "ERROR: GetEnv failed");
        goto bail;
    }
    env = uenv.env;

    if (registerNatives(env) != JNI_TRUE) {
        __android_log_print(4, LOG_TAG, "ERROR: registerNatives failed");
        goto bail;
    }
    
    result = JNI_VERSION_1_4;
    
bail:
    return result;
}

