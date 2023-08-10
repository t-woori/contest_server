INSERT INTO contest(id, created_at, updated_at, deleted_at, running_start_date_time, running_end_date_time, auth_code,
                    host_name, name)
VALUES (0x53A703531F964B3984F922704218627F,
        now(),
        now(),
        null,
        now(),
        TIMESTAMPADD(Minute, 1000, now()),
        '000000',
        'host_name',
        'name'),
       (0xd45fa47fb1de42b29b5982b6cacb1614,
        now(),
        now(),
        null,
        TIMESTAMPADD(Minute, -30, now()),
        TIMESTAMPADD(Minute, -15, now()),
        '000001',
        'ExpiredContest',
        'name'),
       (0x992033a011c945b0a64301a2c706f118,
        now(),
        now(),
        null,
        TIMESTAMPADD(Minute, 1000, now()),
        TIMESTAMPADD(Minute, 2000, now()),
        '000002',
        '000002',
        'EarlyContest');
INSERT INTO student(id, created_at, updated_at, deleted_at, name, email, phone_number, access_token, refresh_token)
VALUES (0xd7762394592c4e338d7106fc5a94abfb, now(), now(), null, 'mockuser', 'mock@mock.com',
        '010-1234-5678', 'mockAccessToken', 'mockRefreshToken');
INSERT INTO student_in_contest(student_id, contest_id, created_at, updated_at, deleted_at)
VALUES (0xd7762394592c4e338d7106fc5a94abfb, 0x53A703531F964B3984F922704218627F, now(), now(), null);
INSERT INTO student_in_contest(student_id, contest_id, created_at, updated_at, deleted_at)
VALUES (0xd7762394592c4e338d7106fc5a94abfb, 0xd45fa47fb1de42b29b5982b6cacb1614, now(), now(), null);
INSERT INTO student_in_contest(student_id, contest_id, created_at, updated_at, deleted_at)
VALUES (0xd7762394592c4e338d7106fc5a94abfb, 0x992033a011c945b0a64301a2c706f118, now(), now(), null);
