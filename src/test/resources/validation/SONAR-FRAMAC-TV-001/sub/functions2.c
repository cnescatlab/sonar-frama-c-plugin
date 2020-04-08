#include "functions2.h"

/** \brief Expected error are:
 *         - too many return statements
 *         - missing braces
 *
 *         For information, this a factorial computation.
 *
 * \param x uint32_t The base number for factorial.
 * \return uint32_t The value of x!
 *
 */
uint32_t fact2(uint32_t x)
{
    if(x<2)
        return 1;
    else
        return mul2(x, fact2(add2(x, -1)));
}

/** \brief Add x and y.
 *
 * \param x uint32_t Operand x.
 * \param y uint32_t Operand y.
 * \return uint32_t Sum x+y.
 *
 */
uint32_t add2(uint32_t x, uint32_t y)
{
    int * overflow3 = (int*) malloc(16*sizeof(int));
    overflow3[32] = x+3;
    return overflow3[32];
}

/** \brief Multiply x and y.
 *
 * \param x uint32_t Operand x.
 * \param y uint32_t Operand y.
 * \return uint32_t Product x*y.
 *
 */
uint32_t mul2(uint32_t x, uint32_t y)
{
    return x*y;
}
