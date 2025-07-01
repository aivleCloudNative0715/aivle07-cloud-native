import React, { useState } from "react";
import { Card, CardContent, CardHeader } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { motion } from "framer-motion";

export function LoginPage() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");

    const handleLogin = async () => {
        console.log("Logging in with", email, password);
    };

    return (
        <motion.div
            className="container mx-auto max-w-md p-4"
            initial={{ opacity: 0, y: 24 }}
            animate={{ opacity: 1, y: 0 }}
        >
            <Card className="shadow-lg">
                <CardHeader>
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
    const [email, setEmail] = useState("");
    const [userName, setUserName] = useState("");
    const [password, setPassword] = useState("");
    const [isKt, setIsKt] = useState(false);

    const handleSignUp = async () => {
        const userData = {
            email,
            userName,
            password,
            isKt,
            myBookHistory: [],
        };
        console.log("Registering", userData);
    };

    return (
        <motion.div
            className="container mx-auto max-w-md p-4"
            initial={{ opacity: 0, y: 24 }}
            animate={{ opacity: 1, y: 0 }}
        >
            <Card className="shadow-lg">
                <CardHeader>
                    <h1 className="text-xl font-bold">회원가입</h1>
                </CardHeader>
                <CardContent className="flex flex-col gap-4">
                    <Input
                        placeholder="이메일 입력"
                        type="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
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
