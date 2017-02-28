The python server:

from pyfcm import FCMNotification
import sys

# My API KEY..
push_service = FCMNotification(<YOUR API KEY HERE>)

# My Device key.......
registration_id = <YOUR DEVICE KEY>
message_title = sys.argv[1]
message_body = sys.argv[2]
result = push_service.notify_single_device(registration_id=registration_id, message_title=message_title, message_body=message_body)
print(result)
