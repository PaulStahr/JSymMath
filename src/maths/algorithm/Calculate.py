
def binarySearch(lowerBound, upperBound, value, eps, functional):
    while abs(upperBound - lowerBound) > eps:
        arg = (lowerBound + upperBound) * 0.5
        res = functional(arg)
        if res < value:
            lowerBound = arg
        elif res > value:
            upperBound = arg
        else:
            return arg
    return (lowerBound + upperBound) * 0.5