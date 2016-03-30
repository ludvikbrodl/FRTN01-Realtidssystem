/** 
 * AVR program for control of the DC-servo process.
 *  
 * User communication via the serial line. Commands:
 *   s: start controller
 *   t: stop controller
 *   r: change sign of reference (+/- 5.0 volt)
 * 
 * To compile for the ATmega8 AVR:
 *   avr-gcc -mmcu=atmega8 -O -g -Wall -o DCservo.elf DCservo.c   
 * 
 * To upload to the ATmega8 AVR:
 *   avr-objcopy -Osrec DCservo.elf DCservo.sr
 *   avrdude -e -p atmega8 -P usb -c avrisp2 -U flash:w:DCServo.sr:a
 * 
 * To compile for the ATmega16 AVR:
 *   avr-gcc -mmcu=atmega16 -O -g -Wall -o DCservo.elf DCservo.c   
 * 
 * To upload to the ATmega16 AVR:
 *   avr-objcopy -Osrec DCservo.elf DCservo.sr
 *   avrdude -e -p atmega16 -P usb -c avrisp2 -U flash:w:DCServo.sr:a
 * 
 * To view the assembler code:
 *   avr-objdump -S DCservo.elf
 * 
 * To open a serial terminal on the PC:
 *   simcom -38400 /dev/ttyS0 
 */

#include <avr/io.h>
#include <avr/interrupt.h>

#define l1 3.2898
#define l2 1.783
#define lr 1.783
#define k1 2.0361
#define k2 1.2440
#define k3 3.2096

#define phi11 0.9940
#define phi12 0
#define phi21 0.2493
#define phi22 1

#define gamma1 0.1122
#define gamma2 0.0140
 




/* Controller parameters and variables (add your own code here) */
int16_t ipart = 0;

int8_t on = 0;                     /* 0=off, 1=on */
int16_t r = 50;                   /* Reference, corresponds to +5.0 V */
float x1, x2, v = 0;



/** 
 * Write a character on the serial connection
 */
static inline void put_char(char ch){
  while ((UCSRA & 0x20) == 0) {};
  UDR = ch;
}

/**
 * Write 10-bit output using the PWM generator
 */
static inline void writeOutput(int16_t val) {
  val += 512;
  OCR1AH = (uint8_t) (val>>8);

  OCR1AL = (uint8_t) val;
}

/**
 * Read 10-bit input using the AD converter
 */
static inline int16_t readInput(char chan) {
  uint8_t low, high;
  ADMUX = 0xc0 + chan;             /* Specify channel (0 or 1) */
  ADCSRA |= 0x40;                  /* Start the conversion */
  while (ADCSRA & 0x40);           /* Wait for conversion to finish */
  low = ADCL;                      /* Read input, low byte first! */
  high = ADCH;                     /* Read input, high byte */
  return ((high<<8) | low) - 512;  /* 10 bit ADC value [-512..511] */ 
}  

/**
 * Interrupt handler for receiving characters over serial connection
 */
ISR(USART_RXC_vect){ 
  switch (UDR) {
  case 's':                        /* Start the controller */
    put_char('s');
    on = 1;
    break;
  case 't':                        /* Stop the controller */
    put_char('t');
    on = 0;
    break;
  case 'r':                        /* Change sign of reference */
    put_char('r');
    r = -r;
    break;
  }
}

/**
 * Interrupt handler for the periodic timer. Interrupts are generated
 * every 10 ms. The control algorithm is executed every 50 ms.
 */
ISR(TIMER2_COMP_vect){
  static int8_t ctr = 0;
  if (++ctr < 5) return;
  ctr = 0;
  if (on) {
    /* Insert your controller code here */
    int16_t y = readInput(1);
  	float u = lr*r - l1*x1 - l2*x2 - v;
    float e = y - x2;
    float x1Temp = x1;
    x1 = phi11*x1 + phi12*x2 + gamma1*(u+v) + k1*e;
    x2 = phi21*x1Temp + phi22*x2 + gamma2*(u+v) + k2*e;
    v = v + k3 * e;

    if(u > 511) {
      u = 511;
    } else if (u < -512) {
      u = -512;
    }

  	writeOutput((int16_t)u);
  } else {                     
    writeOutput(0);     /* Off */
  }
}

/**
 * Main program
 */
int main(){

  DDRB = 0x02;    /* Enable PWM output for ATmega8 */
  DDRD = 0x20;    /* Enable PWM output for ATmega16 */
  DDRC = 0x30;    /* Enable time measurement pins */
  ADCSRA = 0xc7;  /* ADC enable */

  TCCR1A = 0xf3;  /* Timer 1: OC1A & OC1B 10 bit fast PWM */
  TCCR1B = 0x09;  /* Clock / 1 */

  TCNT2 = 0x00;   /* Timer 2: Reset counter (periodic timer) */
  TCCR2 = 0x0f;   /* Clock / 1024, clear after compare match (CTC) */
  OCR2 = 144;     /* Set the compare value, corresponds to ~100 Hz */

  /* Configure serial communication */
  UCSRA = 0x00;   /* USART: */
  UCSRB = 0x98;   /* USART: RXC enable, Receiver enable, Transmitter enable */
  UCSRC = 0x86;   /* USART: 8bit, no parity */
  UBRRH = 0x00;   /* USART: 38400 @ 14.7456MHz */
  UBRRL = 23;     /* USART: 38400 @ 14.7456MHz */

  TIMSK = 1<<OCIE2; /* Start periodic timer */

  sei();          /* Enable interrupts */

  while (1);
}