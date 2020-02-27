% Program to determine the bpm of an ECG signal using two
% methods, autocorrelation and Fourier transform.

clc;
clear all;
close;

% We plot the signal
% 100 Hz every second, 6000 samples so 60 seconds of data
sig = load('ecg.txt');
sig_at_rest = load('Heart_rate_at_rest.txt');
sig_after_activity = load('Heart_rate_aftr_activity.txt');

%sig = sig_at_rest;
val = 7;
sig = sig_after_activity;
 
%subplot(2,1,1);
%plot(sig);
% xlabel('Samples');
% ylabel('Electrical Activity')
% title('ECG signal sampled at 100Hz');

fs = length(sig) / val;
% minimum = min(sig);
% maximum = max(sig);
% average = (minimum + maximum) / 2;
% We run the two methods
%autocorrelation(sig, fs, average)
results = (1:60);
for i = 1:59
    temp_sig=sig(1:i*100);
    temp_fs=length(temp_sig)/i;
    results(i)=fourier_transform(temp_sig, temp_fs,i);
end

results
%test(sig);

L = length(sig);
voltages = 1:L;
heartbeat = 1:L;

function test(sig)
threshold = 300;
L = length(sig);
voltages = 1:L;
heartbeat = 1:L;
intervals = 1:length(heartbeat);

for i = 1 : L
    voltages(i) = sig(i)*1024/5;
    if(sig(i) > threshold)
        heartbeat(i) = sig(i);
    end
end

for j = 1 : length(heartbeat)-1
    intervals(j) = heartbeat(j+1) - heartbeat(j);
end

end

% Using the autocorrelation method
function autocorrelation(sig, fs, average)
    % Count the dominant peaks in the signal (these correspond to heart beats)
    % Peaks are defined to be samples greatet than their two nearest
    % neighbours and greater than 1
    beat_count = 0;
    
    for k = 2 : length(sig) - 1
        if(sig(k) > sig(k - 1) & sig(k) > sig(k + 1) & sig(k) > average)
            %k
            %disp('Prominant peak found');
            beat_count = beat_count + 1;
        end
    end

    % Divide the beats counted by the signal duration (in minutes)
    N = length(sig);
    duration_in_seconds = N / fs;
    duration_in_minutes = duration_in_seconds / 60;
    BPM_autocorrelation = beat_count / duration_in_minutes
end

% Using the Fourier transform
function output = fourier_transform(sig, fs,val)
    y = sig;
    L = length(sig);
    w = [0 :(2*pi/L):2*pi-(2*pi/L)];
    x = fft(y);
    subplot(2,1,2);
    plot(w,abs(x));
    title('FFT');
    %fs = length(y) / 60;
    
    leap = 0;
    pk_i = 1;
    
    while leap <= (length(y)-fs)
        
        for i = 1 : fs
            yy(i) = y(i+leap);
        end
        
        local_i_max = 1;
        local_max = yy(local_i_max);
        
        for i=2:fs
            if local_max<yy(i)
                local_i_max = i;
                local_max=yy(i);
            end
        end
        pk(pk_i)=leap+local_i_max;
        pk_i=pk_i+1;
        leap = floor(leap + fs);
    end
    
    beat = 0;
    beat_i = 1;
    for i=1:length(pk)-1
        if pk(i)<(pk(i+1)-20)    
            beat=beat+(fs/(pk(i+1)-pk(i)))*val;
            beat_i=beat_i+1;
        end
    end
    
    if beat_i>1
    BPM_fft = beat/(beat_i-1);
    BPM_fft = BPM_fft*60/val;
    output =  BPM_fft;
    else
    output = 0;
    end
end
