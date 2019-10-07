
#ifndef LINUX_CM3623_MODULE_H
#define LINUX_CM3623_MODULE_H

/**
 * struct cm3623_platform_data - data to set up cm3623 driver
 *
 * @setup: optional callback to activate the driver.
 * @teardown: optional callback to invalidate the driver.
 *
**/

struct cm3623_platform_data {
    int gpio_int;
    int (*power)(int);
};

#define CM3623_NAME                     "cm3623"
#define CM3623_I2C_ADDR            0X11

#define CM3623_IOC_MAGIC                 'T'

#define CM3623_CALI_0                               _IOR(CM3623_IOC_MAGIC,0, int)
#define CM3623_CALI_20                               _IOR(CM3623_IOC_MAGIC,1, int)
#define CM3623_CALI_35                               _IOR(CM3623_IOC_MAGIC,2, int)

#endif /* LINUX_CM3623_MODULE_H */
