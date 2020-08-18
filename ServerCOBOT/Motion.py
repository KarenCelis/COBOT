import qi
from naoqi import ALProxy

motion = ALProxy("ALMotion", "nao.local", 9559)
motion.setStiffnesses("Body", 1.0)
mo = qi.Session()
