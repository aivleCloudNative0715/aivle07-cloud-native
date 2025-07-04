import React, { useState } from "react";
import { Card, CardContent, CardHeader } from "../components/ui/card";
import { Button } from "../components/ui/button";
import { Input } from "../components/ui/input";
import { motion } from "framer-motion";
import BackButton from "../components/ui/backButton";
import {useAuth} from "../context/AuthContext";
import {useNavigate} from "react-router-dom";

export function LoginPage() {
    const [email, setEmail] = useState("example@gmail.com");
    const [password, setPassword] = useState("1234");
    const { login } = useAuth();
    const navigate = useNavigate();

    const handleLogin = async () => {
        const { success, data } = await login(email, password);
        console.log(data)
        if (success) {
            navigate("/"); // 로그인 후 홈으로 이동
        } else {
            alert("로그인 실패. 이메일/비밀번호를 확인해주세요.");
        }
    };

    return (
        <motion.div
            className="container mx-auto max-w-md p-4"
            initial={{ opacity: 0, y: 24 }}
            animate={{ opacity: 1, y: 0 }}
        >
            <Card className="shadow-lg">
                <BackButton />
                <CardHeader className="flex flex-col gap-2">
                    <h1 className="text-xl font-bold">로그인</h1>
                </CardHeader>
                <CardContent className="flex flex-col gap-4">
                    <Input
                        placeholder="이메일 입력"
                        type="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                    />
                    <Input
                        placeholder="비밀번호 입력"
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                    />
                    <Button onClick={handleLogin}>로그인</Button>
                </CardContent>
            </Card>
        </motion.div>
    );
}

export function SignUpPage() {
    const [email, setEmail] = useState("example@gmail.com");
    const [userName, setUserName] = useState("홍길동");
    const [password, setPassword] = useState("1234");
    const [isKt, setIsKt] = useState(false);
    const { signUp } = useAuth();
    const navigate = useNavigate();

    const handleSignUp = async () => {
        const { success, errorCode} = await signUp({
            email,
            userName,
            password,
            isKt,
        });

        if (success) {
            await new Promise((res) => setTimeout(res, 500));
            alert("회원가입 성공!");
            navigate("/");
        } else {
            if (errorCode === 401) {
                alert("이미 가입된 이메일입니다.");
            } else {
                alert("회원가입 실패. 입력 정보를 다시 확인해주세요.");
            }
        }
    };

    return (
        <motion.div
            className="container mx-auto max-w-md p-4"
            initial={{ opacity: 0, y: 24 }}
            animate={{ opacity: 1, y: 0 }}
        >
            <Card className="shadow-lg">
                <BackButton />
                <CardHeader className="flex flex-col gap-2">
                    <h1 className="text-xl font-bold">회원가입</h1>
                </CardHeader>
                <CardContent className="flex flex-col gap-4">
                    <Input
                        placeholder="이메일 입력"
                        type="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        default
                    />
                    <Input
                        placeholder="사용자 이름 입력"
                        value={userName}
                        onChange={(e) => setUserName(e.target.value)}
                    />
                    <Input
                        placeholder="비밀번호 입력"
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                    />
                    <div className="flex items-center gap-2 text-sm">
                        <input
                            id="isKtCheckbox"
                            type="checkbox"
                            checked={isKt}
                            onChange={(e) => setIsKt(e.target.checked)}
                        />
                        <label htmlFor="isKtCheckbox">KT 회원 여부</label>
                    </div>
                    <Button onClick={handleSignUp}>회원가입</Button>
                </CardContent>
            </Card>
        </motion.div>
    );
}
