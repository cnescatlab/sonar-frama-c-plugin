#include "functions.h"

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
uint32_t fact(uint32_t x)
{
    int * overflow2 = (int*) malloc(5*sizeof(int));
    overflow2[10] = 5;
    if(x<2)
        return 1;
    else
        return mul(x, fact(add(x, -1)));
}

/** \brief Add x and y.
 *
 * \param x uint32_t Operand x.
 * \param y uint32_t Operand y.
 * \return uint32_t Sum x+y.
 *
 */
uint32_t add(uint32_t x, uint32_t y)
{
    return x+y;
}

/** \brief Multiply x and y.
 *
 * \param x uint32_t Operand x.
 * \param y uint32_t Operand y.
 * \return uint32_t Product x*y.
 *
 */
uint32_t mul(uint32_t x, uint32_t y)
{
    return x*y;
}
