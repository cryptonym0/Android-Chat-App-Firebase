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


//////////////////////////////////////////////////////////////////////////////////////////////////////
Or if you want inline
//////////////////////////////////////////////////////////////////////////////////////////////////////

from pyfcm import FCMNotification

push_service = FCMNotification(<YOUR API KEY HERE>)

# My Device key.......
registration_id = <YOUR DEVICE KEY>
message_title = "Dokimomo Chat Beta"
message_body = "Testing the POST"

result = push_service.notify_single_device(registration_id=registration_id, message_title=message_title, message_body=message_body)

#TBA
# Send to multiple devices by passing a list of ids.
# registration_ids = ["<device registration_id 1>", "<device registration_id 2>", ...]
# message_title = "Large Update"
# message_body = "Hope you're having fun this weekend, don't forget to check today's news"
# result = push_service.notify_multiple_devices(registration_ids=registration_ids, message_title=message_title, message_body=message_body)

print(result)
