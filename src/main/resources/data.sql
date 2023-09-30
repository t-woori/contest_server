INSERT INTO contest(id, created_at, updated_at, deleted_at, running_start_date_time, running_end_date_time, auth_code,
                    host_name, name)
VALUES (0xd45fa47fb1de42b29b5982b6cacb1614,
        now(),
        now(),
        null,
        TIMESTAMPADD(Minute, -30, now()),
        TIMESTAMPADD(Minute, -15, now()),
        '000000',
        '00 다문화 센터',
        '00 다문화 센터 1차 대회/종료된 대회'),
       (0x53A703531F964B3984F922704218627F,
        now(),
        now(),
        null,
        now(),
        TIMESTAMPADD(Minute, 1000, now()),
        '000001',
        'XX 다문화 센터',
        'XX 다문화 센터 2차 대회/진행중'),
       (0x992033a011c945b0a64301a2c706f118,
        now(),
        now(),
        null,
        TIMESTAMPADD(Minute, 1000, now()),
        TIMESTAMPADD(Minute, 2000, now()),
        '000002',
        '00 다문화 센터',
        '00 다문화 센터 3차 대회/시작하지 않은 대회'),
       (0xFFBBABA2E0144CF1A254C5634A68B360,
        now(),
        now(),
        null,
        TIMESTAMPADD(MONTH, 2, now()),
        TIMESTAMPADD(MONTH, 3, now()),
        '000003',
        '00 다문화 센터',
        '00 다문화 센터 4차 대회/2개월뒤 시작'),
       (0x27BEAA0A0DD94B118080871A3AAD4F05,
        now(),
        now(),
        null,
        TIMESTAMPADD(DAY, 1, now()),
        TIMESTAMPADD(DAY, 2, now()),
        '000004',
        '00 다문화 센터',
        '00 다문화 센터 5차 대회/하루 뒤에 시작'),
       (0xA3030109B69E417A8B18E2D12A3C33DE,
        now(),
        now(),
        null,
        TIMESTAMPADD(DAY, 1, now()),
        TIMESTAMPADD(DAY, 2, now()),
        '000005',
        '00 다문화 센터',
        '00 다문화 센터 6차 대회/하루 뒤에 시작');
INSERT INTO student(id, created_at, updated_at, deleted_at, name, email, phone_number, access_token, refresh_token)
VALUES (0xd7762394592c4e338d7106fc5a94abfb, now(), now(), null, 'mockuser1', 'mock@mock.com',
        '010-1234-5678', 'mockAccessToken', 'mockRefreshToken');
INSERT INTO student(id, created_at, updated_at, deleted_at, name, email, phone_number, access_token, refresh_token)
VALUES (0xDBA772999009422C91AB7A976525C80A, now(), now(), null, 'mockuser2', 'mock@mock.com',
        '010-1234-5678', 'mockAccessToken', 'mockRefreshToken');
INSERT INTO student_in_contest(student_id, contest_id, created_at, updated_at, deleted_at)
VALUES (0xd7762394592c4e338d7106fc5a94abfb, 0x53A703531F964B3984F922704218627F, now(), now(), null);
INSERT INTO student_in_contest(student_id, contest_id, created_at, updated_at, deleted_at)
VALUES (0xd7762394592c4e338d7106fc5a94abfb, 0xd45fa47fb1de42b29b5982b6cacb1614, now(), now(), null);
INSERT INTO student_in_contest(student_id, contest_id, created_at, updated_at, deleted_at)
VALUES (0xd7762394592c4e338d7106fc5a94abfb, 0x992033a011c945b0a64301a2c706f118, now(), now(), null);
INSERT INTO student_in_contest(student_id, contest_id, created_at, updated_at, deleted_at)
VALUES (0xDBA772999009422C91AB7A976525C80A, 0xA3030109B69E417A8B18E2D12A3C33DE, now(), now(), null);

INSERT INTO problem(id, created_at, updated_at, deleted_at, image_url, grade, problem_type, chapter_type)
VALUES (0, now(), now(), null,
        'https://images.unsplash.com/photo-1588675646184-f5b0b0b0b2de?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1287&q=80',
        0, 0, 0);
INSERT INTO problem(id, created_at, updated_at, deleted_at, grade, problem_type, chapter_type)
VALUES (1, now(), now(), null, 0, 1, 0);
INSERT INTO problem(id, created_at, updated_at, deleted_at, image_url, grade, problem_type, chapter_type)
VALUES (2, now(), now(), null,
        'https://images.unsplash.com/photo-1662837625420-2b5fbaacec98?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1287&q=80',
        1, 0, 1);
INSERT INTO problem(id, created_at, updated_at, deleted_at, image_url, grade, problem_type, chapter_type)
VALUES (3, now(), now(), null,
        null, 1, 1, 1);

INSERT INTO problem(id, created_at, updated_at, deleted_at, image_url, grade, problem_type, chapter_type)
VALUES (4, now(), now(), null,
        'https://images.unsplash.com/photo-1588675646184-f5b0b0b0b2de?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1287&q=80',
        1, 0, 2);
INSERT INTO problem(id, created_at, updated_at, deleted_at, image_url, grade, problem_type, chapter_type)
VALUES (5, now(), now(), null,
        null, 1, 1, 2);

INSERT INTO problem(id, created_at, updated_at, deleted_at, image_url, grade, problem_type, chapter_type)
VALUES (6, now(), now(), null,
        'https://images.unsplash.com/photo-1588675646184-f5b0b0b0b2de?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1287&q=80''',
        1, 0, 3);
INSERT INTO problem(id, created_at, updated_at, deleted_at, image_url, grade, problem_type, chapter_type)
VALUES (7, now(), now(), null,
        null, 1, 1, 3);

INSERT INTO problem(id, created_at, updated_at, deleted_at, image_url, grade, problem_type, chapter_type)
VALUES (8, now(), now(), null,
        'https://images.unsplash.com/photo-1588675646184-f5b0b0b0b2de?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1287&q=80',
        1, 0, 4);
INSERT INTO problem(id, created_at, updated_at, deleted_at, image_url, grade, problem_type, chapter_type)
VALUES (9, now(), now(), null,
        null, 0, 1, 4);


INSERT INTO content(problem_id, content_id, created_at, updated_at, deleted_at, pre_script, question, answer,
                    post_script, hint)
VALUES (0, 0, now(), now(), null, null, '기프티콘으로 다른 __로 변경 가능한가요?', '메뉴', '네 대신 기프티콘 금액보다 적은 건 안되세요', 'ㅁㄴ');
INSERT INTO content(problem_id, content_id, created_at, updated_at, deleted_at, pre_script, question, answer,
                    post_script, hint)
VALUES (0, 1, now(), now(), null, null, '레몬에이드 한 _은 얼마에요?', '잔', '4,000원입니다', 'ㅈ');

INSERT INTO content(problem_id, content_id, created_at, updated_at, deleted_at, pre_script, question, answer,
                    post_script)
VALUES (1, 0, now(), now(), null, null, '어떤 걸로 주문하시겠어요?', '아메리카노 주세요', null);

INSERT INTO content(problem_id, content_id, created_at, updated_at, deleted_at, pre_script, question, answer,
                    post_script, hint)
VALUES (2, 0, now(), now(), null, null, '감기_있나요?', '약', '어떻게 아프신가요.', 'ㅇ');

INSERT INTO content(problem_id, content_id, created_at, updated_at, deleted_at, pre_script, question, answer,
                    post_script, hint)
VALUES (2, 1, now(), now(), null, null, '기침하고 _가 막힙니다.', '코', '종합감기약으로 드리겠습니다', 'ㅋ');

INSERT INTO content(problem_id, content_id, created_at, updated_at, deleted_at, pre_script, question, answer,
                    post_script)
VALUES (3, 0, now(), now(), null, null, '어디가 안 좋으신가요?', '소화제 있나요', null);

INSERT INTO content(problem_id, content_id, created_at, updated_at, deleted_at, pre_script, question, answer,
                    post_script)
VALUES (3, 1, now(), now(), null, null, '약은 짜먹는 약, 알약중 원하는 타입있으신가요?', '짜먹는 약으로 주세요', null);

INSERT INTO content(problem_id, content_id, created_at, updated_at, deleted_at, pre_script, question, answer,
                    post_script, hint)
VALUES (4, 0, now(), now(), null, null, '__ 좀 하려고 왔는데요', '염색', '어떤색 하실거에요', 'ㅇㅅ');

INSERT INTO content(problem_id, content_id, created_at, updated_at, deleted_at, pre_script, question, answer,
                    post_script, hint)
VALUES (4, 1, now(), now(), null, null, '애쉬그레이로 염색하려고 하는데 __을 몇 번 해야 될까요?', '탈색', '지금 모발에서 3번은 들어가야 될 것 같아요', 'ㅌㅅ');

INSERT INTO content(problem_id, content_id, created_at, updated_at, deleted_at, pre_script, question, answer,
                    post_script)
VALUES (5, 0, now(), now(), null, null, '예약하고 오셨나요?', '아니요 예약은 안하고 왔습니다', null);

INSERT INTO content(problem_id, content_id, created_at, updated_at, deleted_at, pre_script, question, answer,
                    post_script)
VALUES (5, 1, now(), now(), null, null, '지금 예약 손님들 먼저 해드리고 있어서 15분 정도 기다리셔야 하는데 괜찮으신가요?', '네 괜찮습니다', null);

INSERT INTO content(problem_id, content_id, created_at, updated_at, deleted_at, pre_script, question, answer,
                    post_script, hint)
VALUES (6, 0, now(), now(), null, null, '한 시간 __은 얼마인가요?', '요금', '1,500원이요', 'ㅇㄱ');

INSERT INTO content(problem_id, content_id, created_at, updated_at, deleted_at, pre_script, question, answer,
                    post_script, hint)
VALUES (6, 1, now(), now(), null, null, '세 명이 나란히 앉을 __ 있나요?', '자리', '네 저쪽에 있습니다', 'ㅈㄹ');

INSERT INTO content(problem_id, content_id, created_at, updated_at, deleted_at, pre_script, question, answer,
                    post_script)
VALUES (7, 0, now(), now(), null, null, '결제는 어떻게 해드릴까요?', '카드로 결제 가능해요', null);

INSERT INTO content(problem_id, content_id, created_at, updated_at, deleted_at, pre_script, question, answer,
                    post_script)
VALUES (7, 1, now(), now(), null, null, '무인 충전기 사용 시 카드 결제도 가능합니다', '무인결제기 사용 방법을 모르는데 도와주실 수 있나요', null);

INSERT INTO content(problem_id, content_id, created_at, updated_at, deleted_at, pre_script, question, answer,
                    post_script, hint)
VALUES (8, 0, now(), now(), null, null, '혼자 __ 가능해요?', '식사', '네 앉으세요', 'ㅅㅅ');

INSERT INTO content(problem_id, content_id, created_at, updated_at, deleted_at, pre_script, question, answer,
                    post_script, hint)
VALUES (8, 1, now(), now(), null, null, '왕돈가스 _은 어느 정도 돼요?', '양', '딱 1인분 정도예요', 'ㅇ');

INSERT INTO content(problem_id, content_id, created_at, updated_at, deleted_at, pre_script, question, answer,
                    post_script)
VALUES (9, 0, now(), now(), null, null, '참치김밥이 제일 잘나가요.  어떠신가요?', '참치김밥으로 주세요.', null);



INSERT INTO problem_in_contest(problem_id, contest_id, created_at, updated_at, deleted_at, no_of_problem_in_contest)
VALUES (0, 0x53A703531F964B3984F922704218627F, now(), now(), null, 0);
INSERT INTO problem_in_contest(problem_id, contest_id, created_at, updated_at, deleted_at, no_of_problem_in_contest)
VALUES (1, 0x53A703531F964B3984F922704218627F, now(), now(), null, 1);
INSERT INTO problem_in_contest(problem_id, contest_id, created_at, updated_at, deleted_at, no_of_problem_in_contest)
VALUES (2, 0x53A703531F964B3984F922704218627F, now(), now(), null, 2);
INSERT INTO problem_in_contest(problem_id, contest_id, created_at, updated_at, deleted_at, no_of_problem_in_contest)
VALUES (3, 0x53A703531F964B3984F922704218627F, now(), now(), null, 3);
INSERT INTO problem_in_contest(problem_id, contest_id, created_at, updated_at, deleted_at, no_of_problem_in_contest)
VALUES (4, 0x53A703531F964B3984F922704218627F, now(), now(), null, 4);
INSERT INTO problem_in_contest(problem_id, contest_id, created_at, updated_at, deleted_at, no_of_problem_in_contest)
VALUES (5, 0x53A703531F964B3984F922704218627F, now(), now(), null, 5);
INSERT INTO problem_in_contest(problem_id, contest_id, created_at, updated_at, deleted_at, no_of_problem_in_contest)
VALUES (6, 0x53A703531F964B3984F922704218627F, now(), now(), null, 6);
INSERT INTO problem_in_contest(problem_id, contest_id, created_at, updated_at, deleted_at, no_of_problem_in_contest)
VALUES (7, 0x53A703531F964B3984F922704218627F, now(), now(), null, 7);
INSERT INTO problem_in_contest(problem_id, contest_id, created_at, updated_at, deleted_at, no_of_problem_in_contest)
VALUES (8, 0x53A703531F964B3984F922704218627F, now(), now(), null, 8);
INSERT INTO problem_in_contest(problem_id, contest_id, created_at, updated_at, deleted_at, no_of_problem_in_contest)
VALUES (9, 0x53A703531F964B3984F922704218627F, now(), now(), null, 9);



