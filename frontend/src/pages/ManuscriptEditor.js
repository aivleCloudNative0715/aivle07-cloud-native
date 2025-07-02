import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { Card, CardContent, CardHeader } from "../components/ui/card";
import { Button } from "../components/ui/button";
import { Input } from "../components/ui/input";
import { Textarea } from "../components/ui/textarea";
import { Loader2, CheckCircle2 } from "lucide-react";
import { motion } from "framer-motion";
import BackButton from "../components/ui/backButton";
import {getStatusLabel} from "../lib/statusUtils";

export default function ManuscriptEditor() {
    const { authorId, manuscriptId } = useParams();
    const isEdit = manuscriptId && manuscriptId !== "new";
    const { user } = useAuth();
    const navigate = useNavigate();
    const API_BASE = process.env.REACT_APP_API_URL;

    const [title, setTitle] = useState("");
    const [body, setBody] = useState("");
    const [status, setStatus] = useState("idle");

    const [authorName, setAuthorName] = useState(user?.username ?? "");
    const [statusText, setStatusText] = useState("");
    const [lastModifiedAt, setLastModifiedAt] = useState("");
    const [summary, setSummary] = useState("");
    const [keywords, setKeywords] = useState("");

    useEffect(() => {
        if (!isEdit) return;

        const fetchDetail = async () => {
            try {
                const res = await fetch(`${API_BASE}/manuscripts/${authorId}/${manuscriptId}`, {
                    headers: {
                        Authorization: `${user.tokenType ?? "Bearer"} ${user.token}`,
                    },
                });
                if (!res.ok) throw new Error("원고를 불러올 수 없습니다.");
                const data = await res.json();
                setTitle(data.title);
                setBody(data.content);
                setAuthorName(data.authorName);
                setStatusText(data.status);
                setLastModifiedAt(data.lastModifiedAt);
                setSummary(data.summary ?? "");
                setKeywords(data.keywords ?? "");
            } catch (e) {
                alert(e.message);
            }
        };

        fetchDetail();
    }, [authorId, manuscriptId, isEdit, API_BASE, user]);

    const handleSave = async () => {
        setStatus("saving");
        try {
            const url = isEdit
                ? `${API_BASE}/manuscripts/${manuscriptId}/save`
                : `${API_BASE}/manuscripts/registration`;

            const method = isEdit ? "PUT" : "POST";

            const payload = {
                authorId: user.userId,
                title,
                content: body,
                summary,
                keywords,
                ...(isEdit ? {} : { authorName }),
            };

            const res = await fetch(url, {
                method,
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `${user.tokenType ?? "Bearer"} ${user.token}`,
                },
                body: JSON.stringify(payload),
            });

            if (!res.ok) throw new Error("저장 실패");
            setStatus("saved");

            setTimeout(() => setStatus("idle"), 2000);
        } catch (e) {
            console.error(e);
            setStatus("idle");
        }
    };

    const handlePublishRequest = async () => {
        setStatus("publishing");
        try {
            const res = await fetch(`${API_BASE}/manuscripts/publication-request`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `${user.tokenType ?? "Bearer"} ${user.token}`,
                },
                body: JSON.stringify({
                    manuscriptId: manuscriptId ?? null,
                    authorId: user.userId,
                }),
            });

            if (!res.ok) throw new Error("출간 요청 실패");

            setStatus("published");

            alert("출간 요청이 완료되었습니다.");

            // 원고 목록 페이지로 이동
            navigate(`/manuscriptList`);
        } catch (e) {
            console.error(e);
            alert("출간 요청 중 오류가 발생했습니다.");
            setStatus("idle");
        }
    };

    return (
        <motion.div
            className="container mx-auto max-w-3xl p-4 grid gap-6"
            initial={{ opacity: 0, y: 24 }}
            animate={{ opacity: 1, y: 0 }}
        >
            <BackButton />
            <Card className="shadow-lg">
                <CardHeader>
                    <h1 className="text-2xl font-bold">
                        {isEdit ? "원고 수정" : "원고 작성"}
                    </h1>
                </CardHeader>

                <CardContent className="flex flex-col gap-4 text-sm">
                    {/* 읽기 전용 정보 */}
                    <p>👤 <strong>작성자:</strong> {authorName}</p>
                    {isEdit && (
                        <>
                            <p>🏷️ <strong>상태:</strong> {getStatusLabel(statusText)}</p>
                            <p>🕒 <strong>최근 수정:</strong> {lastModifiedAt?.split("T")[0]}</p>
                        </>
                    )}

                    {/* 입력 폼 */}
                    <div className="space-y-1">
                        <label htmlFor="title">제목 :</label>
                        <Input
                            id="title"
                            placeholder="제목을 입력하세요"
                            value={title}
                            onChange={(e) => setTitle(e.target.value)}
                        />
                    </div>

                    <div className="space-y-1">
                        <label htmlFor="body">내용 :</label>
                        <Textarea
                            id="body"
                            placeholder="내용을 입력하세요"
                            rows={10}
                            value={body}
                            onChange={(e) => setBody(e.target.value)}
                        />
                    </div>

                    <div className="space-y-1">
                        <label htmlFor="summary">요약 :</label>
                        <Input
                            id="summary"
                            placeholder="요약을 입력하세요 (선택)"
                            value={summary}
                            onChange={(e) => setSummary(e.target.value)}
                        />
                    </div>

                    <div className="space-y-1">
                        <label htmlFor="keywords">키워드 :</label>
                        <Input
                            id="keywords"
                            placeholder="키워드를 입력하세요 (콤마로 구분, 선택)"
                            value={keywords}
                            onChange={(e) => setKeywords(e.target.value)}
                        />
                    </div>

                    {/* 액션 버튼 */}
                    {statusText !== "PUBLICATION_REQUESTED" && (
                        <div className="flex justify-end gap-2 mt-4">
                            <Button
                                disabled={status === "saving" || status === "publishing"}
                                onClick={handleSave}
                            >
                                {status === "saving" && (
                                    <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                                )}
                                저장
                            </Button>

                            <Button
                                disabled={status === "publishing" || status === "saving"}
                                onClick={handlePublishRequest}
                            >
                                {status === "publishing" && (
                                    <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                                )}
                                출간 요청
                            </Button>
                        </div>)
                    }

                    {/* 상태 메시지 */}
                    {status === "saved" && (
                        <p className="flex items-center gap-1 text-sm text-green-600">
                            <CheckCircle2 className="h-4 w-4" /> 임시 저장 완료
                        </p>
                    )}
                    {status === "published" && (
                        <p className="flex items-center gap-1 text-sm text-blue-600">
                            <CheckCircle2 className="h-4 w-4" /> 출간 요청이 접수되었습니다
                        </p>
                    )}
                </CardContent>
            </Card>
        </motion.div>
    );
}
