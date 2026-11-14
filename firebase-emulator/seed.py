"""
Seed the LOCAL Firebase Emulator Suite with demo doctors, patients and appointments.

Only talks to 127.0.0.1 (the emulators). It never touches the real Firebase project.
Usage (with emulators running):  python seed.py
All demo accounts use the password in DEMO_PASSWORD. They exist only inside the emulator.
"""
import json
import os
import sys
import urllib.request
from datetime import datetime, timedelta, timezone

# Project id: first CLI arg, else read from app/google-services.json (git-ignored)
_gs = os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", "app", "google-services.json")
PROJECT_ID = sys.argv[1] if len(sys.argv) > 1 else json.load(open(_gs))["project_info"]["project_id"]
DB_NS = PROJECT_ID + "-default-rtdb"
AUTH = "http://127.0.0.1:9099"
DB = "http://127.0.0.1:9000"
DEMO_PASSWORD = "Demo@1234"

IST = timezone(timedelta(hours=5, minutes=30))


def call(method, url, body=None):
    data = json.dumps(body).encode() if body is not None else None
    req = urllib.request.Request(url, data=data, method=method, headers={
        "Content-Type": "application/json",
        "Authorization": "Bearer owner",  # emulator admin token
    })
    with urllib.request.urlopen(req) as r:
        return json.loads(r.read() or b"null")


def millis(y, m, d, h=0):
    return int(datetime(y, m, d, h, tzinfo=IST).timestamp() * 1000)


MORNING = ["10:00 AM", "10:30 AM", "11:00 AM", "11:30 AM", "12:00 PM"]
EVENING = ["04:00 PM", "04:30 PM", "05:00 PM", "05:30 PM", "06:00 PM"]
DAYS = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"]

DOCTORS = [
    dict(uid="demo-doctor-1", name="Dr. Ananya Sharma", gender="Female", specialization="Cardiologist",
         qualification="MBBS, MD (Cardiology)", experience="12 years", reg="MCI-48213",
         about="Interventional cardiologist focused on preventive heart care and hypertension management.",
         clinic="Heart Care Clinic, MG Road, Bengaluru", days=DAYS[:6], slots=MORNING + EVENING,
         modes={"Clinic Visit": "800rs", "Video Call": "600rs", "Messaging": "300rs"}),
    dict(uid="demo-doctor-2", name="Dr. Rohan Mehta", gender="Male", specialization="Dermatologist",
         qualification="MBBS, MD (Dermatology)", experience="8 years", reg="MCI-55190",
         about="Treats acne, eczema, hair fall and other skin conditions with evidence-based care.",
         clinic="SkinFirst Clinic, Andheri West, Mumbai", days=DAYS[:5], slots=MORNING,
         modes={"Clinic Visit": "700rs", "Voice Call": "400rs", "Video Call": "500rs"}),
    dict(uid="demo-doctor-3", name="Dr. Priya Nair", gender="Female", specialization="Pediatrician",
         qualification="MBBS, DCH", experience="10 years", reg="MCI-61027",
         about="Child specialist for newborn care, vaccinations and growth monitoring.",
         clinic="Little Steps Child Clinic, Kochi", days=DAYS, slots=EVENING,
         modes={"Clinic Visit": "600rs", "Messaging": "250rs", "Video Call": "450rs"}),
    dict(uid="demo-doctor-4", name="Dr. Arjun Verma", gender="Male", specialization="Orthopedic Surgeon",
         qualification="MBBS, MS (Orthopaedics)", experience="15 years", reg="MCI-39874",
         about="Joint replacement and sports injury specialist.",
         clinic="OrthoPlus Hospital, Sector 18, Noida", days=DAYS[:6], slots=MORNING[:3] + EVENING[:3],
         modes={"Clinic Visit": "900rs", "Video Call": "700rs"}),
    dict(uid="demo-doctor-5", name="Dr. Kavya Iyer", gender="Female", specialization="General Physician",
         qualification="MBBS", experience="6 years", reg="MCI-70452",
         about="Family physician for fever, diabetes, thyroid and general health check-ups.",
         clinic="CarePoint Clinic, T. Nagar, Chennai", days=DAYS, slots=MORNING + EVENING,
         modes={"Clinic Visit": "400rs", "Messaging": "150rs", "Voice Call": "250rs", "Video Call": "300rs"}),
]

PATIENTS = [
    dict(uid="demo-patient-1", name="Aarav Gupta", gender="Male", dob=millis(1994, 3, 12), address="Indiranagar, Bengaluru"),
    dict(uid="demo-patient-2", name="Isha Patel", gender="Female", dob=millis(1998, 7, 25), address="Navrangpura, Ahmedabad"),
    dict(uid="demo-patient-3", name="Vikram Singh", gender="Male", dob=millis(1987, 11, 2), address="Malviya Nagar, Jaipur"),
    dict(uid="demo-patient-4", name="Neha Reddy", gender="Female", dob=millis(2000, 1, 18), address="Banjara Hills, Hyderabad"),
    dict(uid="demo-patient-5", name="Kabir Das", gender="Male", dob=millis(1991, 9, 9), address="Salt Lake, Kolkata"),
]

REVIEWS = [
    ("Aarav Gupta", "Very patient and explained everything clearly. Highly recommended!"),
    ("Isha Patel", "Quick consultation and the treatment worked well."),
    ("Neha Reddy", "Friendly doctor, the clinic was clean and on time."),
]


def doctor_doc(d, i):
    slots = {day.lower() + "Slots": (d["slots"] if day in d["days"] else []) for day in DAYS}
    return {
        "name": d["name"], "email": "doctor%d@demo.mydoctor.test" % i, "phone": "+91 90000 0000%d" % i,
        "gender": d["gender"], "specialization": d["specialization"], "qualification": d["qualification"],
        "clinicAddress": d["clinic"], "experience": d["experience"], "aboutMe": d["about"],
        "doctorId": d["uid"], "userType": "Doctor", "medical_reg_no": d["reg"],
        "working_time": "Working Hours | %s - %s" % (d["slots"][0], d["slots"][-1]),
        "dob": millis(1980 + i, 5, 10), "termsAccepted": True, "available": True,
        "consultation_days": d["days"], "holidays": ["25-12-2026"],
        "consultationModesAndCharges": d["modes"],
        "Reviews": {"r%d" % k: {"name": n, "message": m} for k, (n, m) in enumerate(REVIEWS)},
        **slots,
    }


def patient_doc(p, i):
    return {
        "name": p["name"], "email": "patient%d@demo.mydoctor.test" % i, "phone": "+91 80000 0000%d" % i,
        "address": p["address"], "gender": p["gender"], "userType": "Patient", "dob": p["dob"],
        "termsAccepted": True, "available": False,
    }


def appointment(aid, p, d, date, time, mode, completed, problem):
    age = 2026 - datetime.fromtimestamp(p["dob"] / 1000, IST).year
    a = {
        "appointmentId": aid, "patientId": p["uid"], "doctorId": d["uid"],
        "patientName": p["name"], "patientGender": p["gender"], "patientAge": "%d years" % age,
        "patientWeight": "unknown", "patientBloodGroup": "B+", "problemDesc": problem,
        "appointmentMode": mode, "appointmentDate": date, "appointmentTime": time,
        "appointmentCharge": d["modes"][mode], "doctorName": d["name"],
        "doctorSpeciality": d["specialization"], "doctorDegree": d["qualification"],
        "isCompleted": completed,
    }
    if mode == "Clinic Visit":
        a["doctorClinicAddress"] = d["clinic"]
    return a


def main():
    # Fresh start: wipe emulator accounts
    call("DELETE", "%s/emulator/v1/projects/%s/accounts" % (AUTH, PROJECT_ID))

    users = [(d["uid"], "doctor%d@demo.mydoctor.test" % i, d["name"]) for i, d in enumerate(DOCTORS, 1)]
    users += [(p["uid"], "patient%d@demo.mydoctor.test" % i, p["name"]) for i, p in enumerate(PATIENTS, 1)]
    for uid, email, name in users:
        call("POST", "%s/identitytoolkit.googleapis.com/v1/projects/%s/accounts" % (AUTH, PROJECT_ID),
             {"localId": uid, "email": email, "password": DEMO_PASSWORD, "displayName": name})

    D, P = DOCTORS, PATIENTS
    appts = [
        ("demo-appt-1", P[0], D[0], millis(2026, 11, 18, 10), "10:30 AM", "Clinic Visit", False, "Chest discomfort while climbing stairs"),
        ("demo-appt-2", P[0], D[1], millis(2026, 11, 20, 11), "11:00 AM", "Video Call", False, "Skin rash on both arms"),
        ("demo-appt-3", P[0], D[4], millis(2026, 11, 5, 17), "05:00 PM", "Messaging", True, "Fever and body ache"),
        ("demo-appt-4", P[1], D[0], millis(2026, 11, 19, 16), "04:30 PM", "Video Call", False, "High blood pressure follow-up"),
        ("demo-appt-5", P[2], D[0], millis(2026, 11, 21, 12), "12:00 PM", "Messaging", False, "Report review"),
        ("demo-appt-6", P[3], D[2], millis(2026, 11, 17, 17), "05:30 PM", "Clinic Visit", False, "Child vaccination"),
        ("demo-appt-7", P[4], D[3], millis(2026, 11, 3, 10), "10:00 AM", "Clinic Visit", True, "Knee pain after running"),
        ("demo-appt-8", P[3], D[0], millis(2026, 11, 2, 11), "11:30 AM", "Clinic Visit", True, "Routine heart check-up"),
    ]

    db = {
        "Users": {
            "Doctors": {d["uid"]: doctor_doc(d, i) for i, d in enumerate(DOCTORS, 1)},
            "Patients": {p["uid"]: patient_doc(p, i) for i, p in enumerate(PATIENTS, 1)},
        },
        "Appointments": {a[0]: appointment(*a) for a in appts},
        "terms_and_conditions": {
            "Doctor": "Demo terms for doctors: keep your availability and consultation fees up to date.",
            "Patient": "Demo terms for patients: share accurate health details for a correct diagnosis.",
        },
    }
    call("PUT", "%s/.json?ns=%s" % (DB, DB_NS), db)
    print("Seeded %d auth users, %d doctors, %d patients, %d appointments"
          % (len(users), len(DOCTORS), len(PATIENTS), len(appts)))


if __name__ == "__main__":
    main()
