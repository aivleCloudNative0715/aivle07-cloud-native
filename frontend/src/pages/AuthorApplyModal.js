import React, { useState } from "react";

export default function AuthorApplyModal({ onClose, onSubmit, isSubmitting }) {
    const [bio, setBio] = useState("");
    const [portfolio, setPortfolio] = useState("");
    const [representativeWork, setRepresentativeWork] = useState("");

    const handleSubmit = () => {
        if (!bio || !portfolio || !representativeWork) {
            alert("모든 항목을 입력해주세요.");
            return;
        }
        onSubmit({ bio, portfolio, representativeWork });
    };

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 z-50 flex items-center justify-center">
            <div className="bg-white p-6 rounded-md w-[90%] max-w-md shadow">
                <h2 className="text-xl font-semibold mb-4">✍️ 작가 신청</h2>

                <label className="block mb-2 text-sm font-medium">
                    자기소개 (bio)
                    <textarea
                        className="mt-1 w-full border px-3 py-2 rounded"
                        rows={3}
                        value={bio}
                        onChange={(e) => setBio(e.target.value)}
                    />
                </label>

                <label className="block mb-2 text-sm font-medium">
                    대표 작품명
                    <input
                        type="text"
                        className="mt-1 w-full border px-3 py-2 rounded"
                        value={representativeWork}
                        onChange={(e) => setRepresentativeWork(e.target.value)}
                    />
                </label>

                <label className="block mb-4 text-sm font-medium">
                    포트폴리오 링크
                    <input
                        type="text"
                        className="mt-1 w-full border px-3 py-2 rounded"
                        value={portfolio}
                        onChange={(e) => setPortfolio(e.target.value)}
                    />
                </label>

                <div className="flex justify-end gap-2">
                    <button
                        onClick={onClose}
                        className="px-4 py-2 border rounded text-sm"
                    >
                        취소
                    </button>
                    <button
                        onClick={handleSubmit}
                        disabled={isSubmitting}
                        className={`px-4 py-2 text-white rounded text-sm ${isSubmitting ? "bg-gray-400" : "bg-green-600 hover:bg-green-700"}`}
                    >
                        {isSubmitting ? "제출 중..." : "신청"}
                    </button>
                </div>
            </div>
        </div>
    );
}
