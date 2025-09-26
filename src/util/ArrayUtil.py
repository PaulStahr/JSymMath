import inspect
import logging

logger = logging.getLogger(__name__)

def convert(img, module):
    if img is None or module == None or isinstance(img, str):
        return img
    t = type(img)
    if inspect.getmodule(t) == module:
        return img
    if logging.DEBUG >= logging.root.level:
        finfo = inspect.getouterframes(inspect.currentframe())[1]
        logger.log(logging.DEBUG,
                   F'convert {t.__module__} to {module.__name__} by {finfo.filename} line {finfo.lineno}')
    if t.__module__ == 'cupy':
        return module.array(img.get(), copy=False)
    return module.array(img, copy=False)


def getArrayModule(arr):
    if arr is None:
        return None
    t = type(arr)
    if t.__module__ == 'numpy':
        return __import__('numpy')
    if t.__module__ == 'cupy':
        return __import__('cupy')
    return None
