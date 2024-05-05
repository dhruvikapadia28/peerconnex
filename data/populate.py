from faker import Faker
from collections import defaultdict
import random
import re

subjects = ["Math", "Computer Science", "Physics", "Chemistry", "Biology", "Business", "Graphic Design",]
roles = ["Classic Tutor", "Mentor", "Supplemental Instructor"]
time = ["09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "12:00", "12:30", "13:00", "13:30", "14:00", "14:30",
        "15:00", "15:30", "16:00", "16:30", "17:00", "17:30", "18:30"]
weekDays = {"Monday": 0, "Tuesday": 1, "Wednesday":2, "Thursday":3, "Friday": 4}
major = {"Computer Science": ["Computer Science", "Software Engineering", "Computer Engineering"],
         "Math": ["Applied Math", "Statistics", "Pure Math"],
         "Physics" : ["Civil Engineering", "Electrical Engineering"],
         "Chemistry": ["Biochemistry", "Chemical Engineering"],
         "Biology": ["Nursing", "Biologist", "Environmental Science", "Nutrition"],
         "Graphic Design": ["Graphic Design", "Arts"],
         "Business": ["Commerce", "Finance", "Accounting", "Marketing"]}

fgen = Faker()
rand = random.Random()

tutors = {}
tutees = {}
tutee2tutor = defaultdict(list)
payroll = {}


def getPhone():
    phoneNo = fgen.phone_number()
    pattern = "\d{3}-\d{3}-\d{3}"
    m = re.match(pattern, phoneNo)
    while m is None:
        phoneNo = fgen.phone_number()
        m = re.match(pattern, phoneNo)
    return m.group(0)

def build_tutor():
    name = fgen.name()
    nsplit = name.split(" ")
    while len(nsplit) > 2:
        name = ' '.join(nsplit[1:])
        nsplit = name.split(" ")
    fname, lname = name.split(" ")
    id = fgen.ean(8)
    while id in tutors:
        id = fgen.ean(8)
    subject = rand.choice(subjects)
    role = random.choice(roles)
    availability = set()
    hours = set()
    email = (f'{fname}.{lname}@' + fgen.domain_name()).lower()
    count = rand.randint(1, 5)
    while len(availability) != count and len(hours) != count:
        day = fgen.day_of_week()
        time_index = rand.randint(0, len(time)-5)
        working_hours = time[time_index] + "-" + time[time_index+ rand.randint(1,4)]
        hours.add(working_hours)
        if day not in {"Sunday", "Saturday"}:
            availability.add(day)
        wh = dict(zip(list(availability), list(hours)))
    tutors[id] = [name, role, subject, wh, email, getPhone()]

def write_tutors():
    with open("tutors.txt", "w") as outfile:
        for k,v in tutors.items():
            outfile.write(k)
            for j in range(3):
                outfile.write('\t' + v[j])
            outfile.write("\n")

def write_tutor_schedule():
    with open("tutor_schedule.txt", "w") as outfile:
        for k, v in tutors.items():
            for i in v[3]:
                outfile.write(k + "\t" + i + "\t")
                sc = v[3][i].split("-")
                outfile.write(f'{sc[0]}\t{sc[1]}\n')

def payroll_txt():
    wage = dict(zip(roles, [14.75, 14, 14]))
    payroll = {k:str(wage[v[1]]) for k,v in tutors.items()}
    with open("payroll.txt", "w") as outfile:
        for k,v in payroll.items():
            outfile.write(f'{k}\t{v}\n')

def build_tutee():
    name = fgen.name()
    nsplit = name.split(" ")
    while len(nsplit) > 2:
        name = ' '.join(nsplit[1:])
        nsplit = name.split(" ")
    id = fgen.ean(8)
    fname, lname = name.split(" ")
    while id in tutors:
        id = fgen.ean(8)
    subj = rand.choice(subjects)
    email = (f'{fname}.{lname}@' + fgen.domain_name()).lower()
    tutees[id] = [name, rand.choice(major[subj]), email, getPhone()]

def write_phonebook():
    with open("phonebook.txt", "w") as outfile:
        for k,v in tutors.items():
            outfile.write(f'{k}\t{v[-2]}\t{v[-1]}\n')
        for k,v in tutees.items():
            outfile.write(f'{k}\t{v[-2]}\t{v[-1]}\n')

def write_tutee():
    with open("tutee.txt", "w") as outfile:
        for k,v in tutees.items():
            outfile.write(f'{k}\t{v[0]}\t{v[1]}\n')

def build_tutee_session():
    tuteekey = rand.choice(list(tutees.keys()))
    maj = tutees[tuteekey][1]
    for k,v in major.items():
        if maj in v:
            sub = k
    for k,v in tutors.items():
        if sub in v:
            tutorkey = k
    tutee2tutor[tuteekey] += [tutorkey, str(fgen.date_this_year(True, True)), sub, str(rand.randint(0,1))]

def write_sessions():
    with open("appointments.txt", "w") as outfile:
        for k,v in tutee2tutor.items():
            for i in range(0,len(v),4):
                outfile.write(f'{k}\t{v[i]}\t{v[i+1]}\t{v[i+2]}\t{v[i+3]}\n')

def write_review():
    with open("reviews.txt", "w") as outfile:
        for k,v in tutee2tutor.items():
            for i in range(0,len(v),4):
                text = fgen.text(40).replace('\n', ' ')
                outfile.write(f'{v[i]}\t{k}\t{v[i+1]}\t{rand.randint(0,5)}\t{text}\n')

if __name__ == '__main__':
    while len(tutors) != 30 and len(tutees) != 30:
        build_tutor()
        build_tutee()
    for _ in range(30):
        build_tutee_session()
    write_tutors()
    write_tutor_schedule()
    write_tutee()
    payroll_txt()
    write_phonebook()
    write_sessions()
    write_review()