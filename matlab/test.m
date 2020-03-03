clc;
clear all;
close;

sig_at_rest = load('Heart_rate_at_rest.txt');
subplot(2,1,1);
plot(sig_at_rest);
sig_after_activity = load('Heart_rate_aftr_activity.txt');
subplot(2,1,2);
plot(sig_after_activity);

sig = sig_at_rest;
%sig = sig_after_activity;

% L = length(sig);
% voltages = 1:L;
% threshold = 330;
% BPM = 0;
% 
% for i = 1 : L
%     voltages(i) = sig(i)*1024/5;
%     
%     if(voltages(i) > threshold)
%         BPM = BPM + 1;
%     end
% end
% BPM
L = length(sig);
beat_count = 0;
fs = L / 60
average = 0;
for i = 1 : L
    average = average + sig(i);
end
threshold = average/L

for k = 2 : L - 1
    if(sig(k) > sig(k - 1) & sig(k) > sig(k + 1) & sig(k) > threshold)
        beat_count = beat_count + 1;
    end
end

beat_count

% duration_in_seconds = L / fs;
% duration_in_minutes = duration_in_seconds / 60;
% BPM = beat_count / duration_in_minutes