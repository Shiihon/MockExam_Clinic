## 1.5.4 endpoints results

**Get all doctors :**

HTTP/1.1 200 OK
Date: Thu, 31 Oct 2024 16:06:59 GMT
Content-Type: application/json
Content-Length: 1026

[
{
"id": 3,
"name": "Dr. Clara Lee",
"birthDate": [
1983,
7,
22
],
"yearOfGraduation": 2008,
"clinicName": "Green Valley Hospital",
"speciality": "PEDIATRICS"
},
{
"id": 4,
"name": "Dr. David Park",
"birthDate": [
1978,
11,
15
],
"yearOfGraduation": 2003,
"clinicName": "Hillside Medical Practice",
"speciality": "PSYCHIATRY"
},
{
"id": 6,
"name": "Dr. Fiona Martinez",
"birthDate": [
1985,
2,
17
],
"yearOfGraduation": 2010,
"clinicName": "Riverside Wellness Clinic",
"speciality": "SURGERY"
},
{
"id": 1,
"name": "Dr. Alice Smith",
"birthDate": [
1975,
4,
12
],
"yearOfGraduation": 2000,
"clinicName": "City Health Clinic",
"speciality": "FAMILY_MEDICINE"
},
{
"id": 7,
"name": "Dr. George Kim",
"birthDate": [
1979,
5,
29
],
"yearOfGraduation": 2004,
"clinicName": "Summit Health Institute",
"speciality": "FAMILY_MEDICINE"
},
{
"id": 2,
"name": "Dr. Bob Johnson",
"birthDate": [
1980,
8,
5
],
"yearOfGraduation": 2005,
"clinicName": "Downtown Medical Center",
"speciality": "SURGERY"
},
{
"id": 5,
"name": "Dr. Emily White",
"birthDate": [
1982,
9,
30
],
"yearOfGraduation": 2007,
"clinicName": "Metro Health Center",
"speciality": "PEDIATRICS"
}
]
Response file saved.
> 2024-10-31T170659.200.json

Response code: 200 (OK); Time: 381ms (381 ms); Content length: 1026 bytes (1,03 kB)

<hr>

**Get doctor by id :**

HTTP/1.1 200 OK
Date: Thu, 31 Oct 2024 16:09:17 GMT
Content-Type: application/json
Content-Length: 142

{
"id": 2,
"name": "Dr. Bob Johnson",
"birthDate": [
1980,
8,
5
],
"yearOfGraduation": 2005,
"clinicName": "Downtown Medical Center",
"speciality": "SURGERY"
}
Response file saved.
> 2024-10-31T170917.200.json

Response code: 200 (OK); Time: 9ms (9 ms); Content length: 142 bytes (142 B)

<hr>

**Get doctor by speciality :**

HTTP/1.1 200 OK
Date: Thu, 31 Oct 2024 16:19:47 GMT
Content-Type: application/json
Content-Length: 102

{
"id": 1,
"name": "newName",
"birthDate": null,
"yearOfGraduation": null,
"clinicName": null,
"speciality": null
}
Response file saved.
> 2024-10-31T171947.200.json

Response code: 200 (OK); Time: 37ms (37 ms); Content length: 102 bytes (102 B)

<hr>

**Update doctor**

HTTP/1.1 200 OK
Date: Thu, 31 Oct 2024 16:20:50 GMT
Content-Type: application/json
Content-Length: 102

{
"id": 1,
"name": "newName",
"birthDate": null,
"yearOfGraduation": null,
"clinicName": null,
"speciality": null
}
Response file saved.
> 2024-10-31T172050.200.json

Response code: 200 (OK); Time: 7ms (7 ms); Content length: 102 bytes (102 B)

_// NOTE: forgot that I had to write everything in order to update, and not just one single thing as name, this is changed when the real 
DAO gets implemented_. 

<hr>

**Get doctors by birthrange :**

HTTP/1.1 200 OK
Date: Thu, 31 Oct 2024 16:28:34 GMT
Content-Type: application/json
Content-Length: 897

[
{
"id": 3,
"name": "Dr. Clara Lee",
"speciality": "PEDIATRICS",
"birthdate": [
1983,
7,
22
],
"year_of_graduation": 2008,
"clinic_name": "Green Valley Hospital"
},
{
"id": 4,
"name": "Dr. David Park",
"speciality": "PSYCHIATRY",
"birthdate": [
1978,
11,
15
],
"year_of_graduation": 2003,
"clinic_name": "Hillside Medical Practice"
},
{
"id": 6,
"name": "Dr. Fiona Martinez",
"speciality": "SURGERY",
"birthdate": [
1985,
2,
17
],
"year_of_graduation": 2010,
"clinic_name": "Riverside Wellness Clinic"
},
{
"id": 7,
"name": "Dr. George Kim",
"speciality": "FAMILY_MEDICINE",
"birthdate": [
1979,
5,
29
],
"year_of_graduation": 2004,
"clinic_name": "Summit Health Institute"
},
{
"id": 2,
"name": "Dr. Bob Johnson",
"speciality": "SURGERY",
"birthdate": [
1980,
8,
5
],
"year_of_graduation": 2005,
"clinic_name": "Downtown Medical Center"
},
{
"id": 5,
"name": "Dr. Emily White",
"speciality": "PEDIATRICS",
"birthdate": [
1982,
9,
30
],
"year_of_graduation": 2007,
"clinic_name": "Metro Health Center"
}
]
Response file saved.
> 2024-10-31T172834.200.json

Response code: 200 (OK); Time: 15ms (15 ms); Content length: 897 bytes (897 B)

<hr>

**Create new doctor :**

HTTP/1.1 201 Created
Date: Thu, 31 Oct 2024 16:29:28 GMT
Content-Type: application/json
Content-Length: 126

{
"id": 8,
"name": "testName",
"speciality": "SURGERY",
"birthdate": [
1975,
4,
12
],
"year_of_graduation": 2024,
"clinic_name": "testClinic"
}
Response file saved.
> 2024-10-31T172928.201.json

Response code: 201 (Created); Time: 6ms (6 ms); Content length: 126 bytes (126 B)

<hr>


**Provoked 404 - not foun :**

HTTP/1.1 404 Not Found
Date: Thu, 31 Oct 2024 16:39:31 GMT
Content-Type: application/json
Content-Length: 119

{
"error message": {
"status": 404,
"message": "doctor with id 50 could not be found"
},
"time of error": "2024-10-31 17:39:31"
}
Response file saved.
> 2024-10-31T173931.404.json

Response code: 404 (Not Found); Time: 114ms (114 ms); Content length: 119 bytes (119 B)

<hr>

## 3.2 Generics

Generics allows to create flexible and reusable code by letting our interface handle the different datatypes, 
like DoctorDTO and Speciality.
Making it generic reduces redundancy and improves type safety, while the codebase becomes easier to maintain, and add to.

<hr>





