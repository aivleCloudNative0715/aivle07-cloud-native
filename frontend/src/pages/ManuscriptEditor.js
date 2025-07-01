import React, { useState } from "react";
import { Card, CardContent, CardHeader } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Loader2, CheckCircle2, UploadCloud } from "lucide-react";
import { motion } from "framer-motion";

/**
 * 원고 작성/수정 화면
 * - 제목, 본문, 파일 첨부(선택) 입력
 * - "저장"  : 임시 저장 API 호출
 * - "출간 요청": 출간 요청 API 호출
 */
export default function ManuscriptEditor() {
    const [title, setTitle] = useState("");
    const [body, setBody] = useState("");
    const [file, setFile] = useState<File | null>(null);
    const [status, setStatus] = useState<
    "idle" | "saving" | "saved" | "publishing" | "published"
    >("idle");

    /** 파일 선택 */
    const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        if (e.target.files?.[0]) {
            setFile(e.target.files[0]);
        }
    };

    /** 원고 임시 저장 */
    const handleSave = async () => {
        setStatus("saving");
        try {
            // TODO: saveDraft(title, body, file)
            await new Promise((r) => setTimeout(r, 1500));
            setStatus("saved");
        } catch (e) {
            console.error(e);
            setStatus("idle");
        }
    };

    /** 출간 요청 */
    const handlePublishRequest = async () => {
        setStatus("publishing");
        try {
            // TODO: publishRequest(title, body, file)
            await new Promise((r) => setTimeout(r, 2000));
            setStatus("published");
        } catch (e) {
            console.error(e);
            setStatus("idle");
        }
    };

    return (
        <motion.div
            className="container mx-auto max-w-3xl p-4 grid gap-6"
            initial={{ opacity: 0, y: 24 }}
            animate={{ opacity: 1, y: 0 }}
        >
            <Card className="shadow-lg">
                <CardHeader>
                    <h1 className="text-2xl font-bold">원고 작성</h1>
                </CardHeader>
                <CardContent className="flex flex-col gap-4">
                    <Input
                        placeholder="제목을 입력하세요"
                        value={title}
                        onChange={(e) => setTitle(e.target.value)}
                    />
                    <Textarea
                        placeholder="내용을 입력하세요"
                        rows={18}
                        value={body}
                        onChange={(e) => setBody(e.target.value)}
                    />

                    {/* 파일 업로드 */}
                    <div className="flex items-center gap-4">
                        <label className="flex cursor-pointer items-center gap-2 text-sm">
                            <UploadCloud className="h-4 w-4" />
                            <span>{file ? file.name : "첨부파일 업로드"}</span>
                            <input
                                type="file"
                                accept=".doc,.docx,.pdf,.txt"
                                onChange={handleFileChange}
                                className="hidden"
                            />
                        </label>
                    </div>

                    {/* 액션 버튼 */}
                    <div className="flex justify-end gap-2">
                        <Button
                            variant="secondary"
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
                    </div>

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
