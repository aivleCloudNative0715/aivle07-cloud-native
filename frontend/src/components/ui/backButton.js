import React from "react";
import { useNavigate } from "react-router-dom";
import { Button } from "./button";

export default function BackButton({ className = "m-1", label = "← 뒤로가기" }) {
    const navigate = useNavigate();

    return (
        <Button variant="ghost" onClick={() => navigate(-1)} className={className}>
            {label}
        </Button>
    );
}